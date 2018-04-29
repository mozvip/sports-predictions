package predictions.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import predictions.model.db.UserDAO;

@Path("/email")
@Api
public class ValidateEmailResource {
	
	private UserDAO userDAO;
	
	@Context private HttpServletRequest httpRequest;
	
	public ValidateEmailResource( UserDAO userDAO ) {
		this.userDAO = userDAO;
	}

	@GET
	@Path("/valid")
	@ApiOperation("Indicates if the email is available for new users")
	public Response isAvailableForNewAccount(@QueryParam("email") String email) {
		String community = (String) httpRequest.getAttribute("community");
		
		if (userDAO.findExistingUserByEmail(community, email) != null) {
			return Response.status(Status.CONFLICT).build();
		}
		
		// FIXME : externalize

		if (!email.endsWith("@yopmail.com")) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
		
		if (!email.endsWith("@yopmail.fr")) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		if (community.equalsIgnoreCase("grand-est")) {
			if (!email.endsWith("@cgi.com")) {
				return Response.status(Status.NOT_ACCEPTABLE).build();
			}
		}

		return Response.accepted().build();
	}
	

}
