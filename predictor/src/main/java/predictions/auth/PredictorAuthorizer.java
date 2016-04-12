package predictions.auth;

import io.dropwizard.auth.Authorizer;
import predictions.model.User;

public class PredictorAuthorizer implements Authorizer<User>{

	public boolean authorize(User principal, String role) {
		// TODO Auto-generated method stub
		return false;
	}

}
