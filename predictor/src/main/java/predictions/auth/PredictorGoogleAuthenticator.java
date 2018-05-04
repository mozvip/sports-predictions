package predictions.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

public class PredictorGoogleAuthenticator implements Authenticator<CommunityOAuthCredentials, User> {

	private UserDAO dao;
	private GoogleIdTokenVerifier verifier;
	
	public PredictorGoogleAuthenticator(String clientId, UserDAO dao) {

		this.dao = dao;

		HttpTransport transport = new NetHttpTransport.Builder().build();
		JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(clientId))
				// Or, if multiple clients access the backend:
				//.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
				.build();
	}

	public Optional<User> authenticate(CommunityOAuthCredentials credentials)
			throws AuthenticationException {

		GoogleIdToken idToken;
		try {
			idToken = verifier.verify(credentials.getToken());
		} catch (GeneralSecurityException | IOException e) {
			throw  new AuthenticationException(e.getMessage(), e);
		}
		if (idToken != null) {
			GoogleIdToken.Payload payload = idToken.getPayload();

			// Print user identifier
			String userId = payload.getSubject();
			System.out.println("User ID: " + userId);

			// Get profile information from payload
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");

			// Use or store profile information
			// ...

			User user = dao.findUser(credentials.getCommunity(), email);
			return Optional.of(user);

		}
        return Optional.empty();
	}

}
