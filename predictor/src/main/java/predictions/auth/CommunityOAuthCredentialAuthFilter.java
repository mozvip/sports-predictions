package predictions.auth;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import predictions.model.db.User;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

public class CommunityOAuthCredentialAuthFilter extends AuthFilter<CommunityOAuthCredentials, User> {

    private String defaultCommunity;

    /**
     * Query parameter used to pass Bearer token
     *
     * @see <a href="https://tools.ietf.org/html/rfc6750#section-2.3">The OAuth 2.0 Authorization Framework: Bearer Token Usage</a>
     */
    public static final String OAUTH_ACCESS_TOKEN_PARAM = "access_token";

    private CommunityOAuthCredentialAuthFilter(String defaultCommunity) {
        this.defaultCommunity = defaultCommunity;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {

        // FIXME: get community
        CommunityOAuthCredentials credentials = getCredentials(defaultCommunity, requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        // If Authorization header is not used, check query parameter where token can be passed as well
        if (credentials == null) {
            credentials = getCredentials(defaultCommunity, requestContext.getUriInfo().getQueryParameters().getFirst(OAUTH_ACCESS_TOKEN_PARAM));
        }

        if (!authenticate(requestContext, credentials, SecurityContext.BASIC_AUTH)) {
            throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
        }
    }

    /**
     * Parses a value of the `Authorization` header in the form of `Bearer a892bf3e284da9bb40648ab10`.
     *
     * @param header the value of the `Authorization` header
     * @return a token
     */
    @Nullable
    private CommunityOAuthCredentials getCredentials(String community, String header) {
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

        return new CommunityOAuthCredentials(community, header.substring(space + 1));
    }

    /**
     * Builder for {@link BasicCredentialAuthFilter}.
     * <p>An {@link Authenticator} must be provided during the building process.</p>
     */
    public static class Builder extends
            AuthFilterBuilder<CommunityOAuthCredentials, predictions.model.db.User, CommunityOAuthCredentialAuthFilter> {

        private String defaultCommunity;

        public Builder(String defaultCommunity) {
            this.defaultCommunity = defaultCommunity;
        }

        @Override
        protected CommunityOAuthCredentialAuthFilter newInstance() {
            return new CommunityOAuthCredentialAuthFilter(defaultCommunity);
        }
    }
}
