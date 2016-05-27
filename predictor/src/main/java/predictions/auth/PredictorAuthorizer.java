package predictions.auth;

import io.dropwizard.auth.Authorizer;
import predictions.model.User;

public class PredictorAuthorizer implements Authorizer<User>{

	public boolean authorize(User principal, String role) {
		if (role.equalsIgnoreCase("ADMIN")) {
			// everyone is an ADMIN on localhost
			return principal.getCommunity().equals("localhost") || principal.isAdmin();
		}
		return true;
	}

}
