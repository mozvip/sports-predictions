package predictions.auth;

import io.dropwizard.auth.Authorizer;
import predictions.model.db.User;

import java.util.List;
import java.util.Set;

public class PredictorAuthorizer implements Authorizer<User>{

	private Set<String> administratorAccounts;

	public PredictorAuthorizer(Set<String> administratorAccounts) {
		this.administratorAccounts = administratorAccounts;
	}

	public boolean authorize(User principal, String role) {
		if (role.equalsIgnoreCase("ADMIN")) {
			// everyone is an ADMIN on localhost
			return principal.getCommunity().equals("localhost") || principal.isAdmin() || administratorAccounts.contains(principal.getEmail());
		}
		return true;
	}

}
