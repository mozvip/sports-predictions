package predictions.auth;

import com.google.common.io.BaseEncoding;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.annotation.Nullable;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION)
public class CommunityBasicCredentialAuthFilter<P extends Principal> extends AuthFilter<CommunityBasicCredentials, P> {

    private String defaultCommunity;

    private CommunityBasicCredentialAuthFilter(String defaultCommunity) {
        this.defaultCommunity = defaultCommunity;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
       	String community = CommunityFilter.extractCommunity(requestContext.getUriInfo().getRequestUri().getHost(), defaultCommunity);
        final CommunityBasicCredentials credentials = getCredentials(community, requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (!authenticate(requestContext, credentials, SecurityContext.BASIC_AUTH)) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
    }

    /**
     * Parses a Base64-encoded value of the `Authorization` header
     * in the form of `Basic dXNlcm5hbWU6cGFzc3dvcmQ=`.
     *
     * @param header the value of the `Authorization` header
     * @return a username and a password as {@link BasicCredentials}
     */
    @Nullable
    private CommunityBasicCredentials getCredentials(String community, String header) {
        if (header == null) {
            return null;
        }

        final int space = header.indexOf(' ');
        if (space <= 0) {
            return null;
        }

        final String method = header.substring(0, space);
        if (!prefix.equalsIgnoreCase(method)) {
            return null;
        }

        final String decoded;
        try {
            decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            logger.warn("Error decoding credentials", e);
            return null;
        }

        // Decoded credentials is 'username:password'
        final int i = decoded.indexOf(':');
        if (i <= 0) {
            return null;
        }

        final String username = decoded.substring(0, i);
        final String password = decoded.substring(i + 1);
        return new CommunityBasicCredentials(community, username, password);
    }

    /**
     * Builder for {@link BasicCredentialAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     *
     * @param <P> the principal
     */
    public static class Builder<P extends Principal> extends
            AuthFilterBuilder<CommunityBasicCredentials, P, CommunityBasicCredentialAuthFilter<P>> {

        private String defaultCommunity;

        public Builder(String defaultCommunity) {
            this.defaultCommunity = defaultCommunity;
        }

        @Override
        protected CommunityBasicCredentialAuthFilter<P> newInstance() {
            return new CommunityBasicCredentialAuthFilter<>(defaultCommunity);
        }
    }
}
