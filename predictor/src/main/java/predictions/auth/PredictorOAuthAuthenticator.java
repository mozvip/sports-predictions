package predictions.auth;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import predictions.model.User;
import predictions.model.UserDAO;

public class PredictorOAuthAuthenticator implements Authenticator<String, User> {
	
	public final static String COMMUNITY_EMAIL_SEPARATOR = "$$_$$";
	
	private UserDAO dao = null;
	
	public PredictorOAuthAuthenticator( UserDAO dao ) {
		this.dao = dao;
	}

	public Optional<User> authenticate(String token)
			throws AuthenticationException {
		
//		String username = credentials.getUsername();
//		int i = username.indexOf( COMMUNITY_EMAIL_SEPARATOR );
//		
//		String community = username.substring( 0, i );
//		String email = username.substring( i + 5 ).toUpperCase().trim();
//
//		User user = dao.authentify( community, email, credentials.getPassword() );
//		if (user != null) {
//			return Optional.of( user );
//		}
        return Optional.empty();
	}

}
