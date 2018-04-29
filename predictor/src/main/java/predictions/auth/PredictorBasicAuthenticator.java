package predictions.auth;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

public class PredictorBasicAuthenticator implements Authenticator<CommunityBasicCredentials, User> {

	private UserDAO dao = null;
	
	public PredictorBasicAuthenticator( UserDAO dao ) {
		this.dao = dao;
	}

	public Optional<User> authenticate(CommunityBasicCredentials credentials)
			throws AuthenticationException {
		User user = dao.authentify( credentials.getCommunity(), credentials.getUsername(), credentials.getPassword() );
		if (user != null) {
			dao.updateLastLoginDate( credentials.getCommunity(), credentials.getUsername() );
			return Optional.of( user );
		}
        return Optional.empty();
	}

}
