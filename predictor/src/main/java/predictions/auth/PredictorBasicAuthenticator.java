package predictions.auth;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import predictions.model.User;
import predictions.model.UserDAO;

public class PredictorBasicAuthenticator implements Authenticator<BasicCredentials, User> {
	
	public final static String COMMUNITY_EMAIL_SEPARATOR = "$$_$$";
	
	private UserDAO dao = null;
	
	public PredictorBasicAuthenticator( UserDAO dao ) {
		this.dao = dao;
	}

	public Optional<User> authenticate(BasicCredentials credentials)
			throws AuthenticationException {
		
		String username = credentials.getUsername();
		int i = username.indexOf( COMMUNITY_EMAIL_SEPARATOR );
		
		String community = username.substring( 0, i );
		String email = username.substring( i + 5 ).toUpperCase().trim();

		User user = dao.authentify( community, email, credentials.getPassword() );
		if (user != null) {
			return Optional.of( user );
		}
        return Optional.empty();
	}

}
