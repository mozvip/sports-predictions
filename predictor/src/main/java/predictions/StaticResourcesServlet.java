package predictions;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import io.dropwizard.servlets.assets.ByteRange;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class StaticResourcesServlet extends HttpServlet {

    private static final CharMatcher SLASHES = CharMatcher.is('/');

    private static class CachedAsset {
        private final byte[] resource;
        private final String eTag;
        private final long lastModifiedTime;

        private CachedAsset(byte[] resource, long lastModifiedTime) {
            this.resource = resource;
            this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
            this.lastModifiedTime = lastModifiedTime;
        }

        public byte[] getResource() {
            return resource;
        }

        public String getETag() {
            return eTag;
        }

        public long getLastModifiedTime() {
            return lastModifiedTime;
        }
    }

    private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;

    private final Path localResourcePath;
    private final String uriPath;

    @Nullable
    private final String indexFile;

    @Nullable
    private final Charset defaultCharset;

    /**
     * Creates a new {@code StaticResourcesServlet} that serves static assets loaded from {@code resourceURL}
     * (typically a file: or jar: URL). The assets are served at URIs rooted at {@code uriPath}. For
     * example, given a {@code resourceURL} of {@code "file:/data/assets"} and a {@code uriPath} of
     * {@code "/js"}, an {@code AssetServlet} would serve the contents of {@code
     * /data/assets/example.js} in response to a request for {@code /js/example.js}. If a directory
     * is requested and {@code indexFile} is defined, then {@code AssetServlet} will attempt to
     * serve a file with that name in that directory. If a directory is requested and {@code
     * indexFile} is null, it will serve a 404.
     *
     * @param localResourcePath   the local folder from which assets are loaded
     * @param uriPath        the URI path fragment in which all requests are rooted
     * @param indexFile      the filename to use when directories are requested, or null to serve no
     *                       indexes
     * @param defaultCharset the default character set
     */
    public StaticResourcesServlet(Path localResourcePath,
                        String uriPath,
                        @Nullable String indexFile,
                        @Nullable Charset defaultCharset) {
        this.localResourcePath = localResourcePath;
        final String trimmedUri = SLASHES.trimTrailingFrom(uriPath);
        this.uriPath = trimmedUri.isEmpty() ? "/" : trimmedUri;
        this.indexFile = indexFile;
        this.defaultCharset = defaultCharset;
    }

    public String getUriPath() {
        return uriPath;
    }

    @Nullable
    public String getIndexFile() {
        return indexFile;
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        try {
            final StringBuilder builder = new StringBuilder(req.getServletPath());
            if (req.getPathInfo() != null) {
                builder.append(req.getPathInfo());
            }
            final StaticResourcesServlet.CachedAsset cachedAsset = loadAsset(builder.toString());
            if (cachedAsset == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (isCachedClientSide(req, cachedAsset)) {
                resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            final String rangeHeader = req.getHeader(HttpHeaders.RANGE);

            final int resourceLength = cachedAsset.getResource().length;
            ImmutableList<ByteRange> ranges = ImmutableList.of();

            boolean usingRanges = false;
            // Support for HTTP Byte Ranges
            // http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
            if (rangeHeader != null) {

                final String ifRange = req.getHeader(HttpHeaders.IF_RANGE);

                if (ifRange == null || cachedAsset.getETag().equals(ifRange)) {

                    try {
                        ranges = parseRangeHeader(rangeHeader, resourceLength);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    if (ranges.isEmpty()) {
                        resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    usingRanges = true;

                    resp.addHeader(HttpHeaders.CONTENT_RANGE, "bytes "
                            + Joiner.on(",").join(ranges) + "/" + resourceLength);
                }
            }

            resp.setDateHeader(HttpHeaders.LAST_MODIFIED, cachedAsset.getLastModifiedTime());
            resp.setHeader(HttpHeaders.ETAG, cachedAsset.getETag());

            final String mimeTypeOfExtension = req.getServletContext()
                    .getMimeType(req.getRequestURI());
            MediaType mediaType = DEFAULT_MEDIA_TYPE;

            if (mimeTypeOfExtension != null) {
                try {
                    mediaType = MediaType.parse(mimeTypeOfExtension);
                    if (defaultCharset != null && mediaType.is(MediaType.ANY_TEXT_TYPE)) {
                        mediaType = mediaType.withCharset(defaultCharset);
                    }
                } catch (IllegalArgumentException ignore) {
                    // ignore
                }
            }

            if (mediaType.is(MediaType.ANY_VIDEO_TYPE)
                    || mediaType.is(MediaType.ANY_AUDIO_TYPE) || usingRanges) {
                resp.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
            }

            resp.setContentType(mediaType.type() + '/' + mediaType.subtype());

            if (mediaType.charset().isPresent()) {
                resp.setCharacterEncoding(mediaType.charset().get().toString());
            }

            try (ServletOutputStream output = resp.getOutputStream()) {
                if (usingRanges) {
                    for (ByteRange range : ranges) {
                        output.write(cachedAsset.getResource(), range.getStart(),
                                range.getEnd() - range.getStart() + 1);
                    }
                } else {
                    output.write(cachedAsset.getResource());
                }
            }
        } catch (RuntimeException ignored) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Nullable
    private StaticResourcesServlet.CachedAsset loadAsset(String key) throws IOException {
        checkArgument(key.startsWith(uriPath));
        final String requestedResourcePath = SLASHES.trimFrom(key.substring(uriPath.length()));
        Path absoluteRequestedResourcePath = this.localResourcePath.resolve(requestedResourcePath);

        if (!Files.exists(absoluteRequestedResourcePath)) {
            return null;
        }

        if (Files.isDirectory(absoluteRequestedResourcePath)) {
            if (indexFile != null) {
                absoluteRequestedResourcePath = absoluteRequestedResourcePath.resolve(indexFile);
            } else {
                // directory requested but no index file defined
                return null;
            }
        }

        FileTime lastModified = Files.getLastModifiedTime(absoluteRequestedResourcePath);

        // zero out the millis since the date we get back from If-Modified-Since will not have them
        long millis = lastModified.toMillis();
        millis = (millis / 1000) * 1000;
        return new StaticResourcesServlet.CachedAsset(readResource(absoluteRequestedResourcePath), millis);
    }

    protected byte[] readResource(Path resourcePath) throws IOException {
        return Files.readAllBytes(resourcePath);
    }

    private boolean isCachedClientSide(HttpServletRequest req, StaticResourcesServlet.CachedAsset cachedAsset) {
        return cachedAsset.getETag().equals(req.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
                (req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= cachedAsset.getLastModifiedTime());
    }

    /**
     * Parses a given Range header for one or more byte ranges.
     *
     * @param rangeHeader Range header to parse
     * @param resourceLength Length of the resource in bytes
     * @return List of parsed ranges
     */
    private ImmutableList<ByteRange> parseRangeHeader(final String rangeHeader,
                                                      final int resourceLength) {
        final ImmutableList.Builder<ByteRange> builder = ImmutableList.builder();
        if (rangeHeader.contains("=")) {
            final String[] parts = rangeHeader.split("=");
            if (parts.length > 1) {
                final List<String> ranges = Splitter.on(",").trimResults().splitToList(parts[1]);
                for (final String range : ranges) {
                    builder.add(ByteRange.parse(range, resourceLength));
                }
            }
        }
        return builder.build();
    }
}
