package predictions.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.dropwizard.auth.Auth;
import predictions.model.User;

@Path("/test")
public class TestAuthResource {
	
	@GET
	public void test(@Auth User user) {
		System.out.println( user.getName() );
	}

}
