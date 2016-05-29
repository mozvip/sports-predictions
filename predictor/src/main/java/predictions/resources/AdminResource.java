package predictions.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import predictions.model.User;
import predictions.model.UserDAO;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

	private UserDAO userDAO;

	@Context private HttpServletRequest httpRequest;

	public AdminResource( UserDAO userDAO ) {
		this.userDAO = userDAO;
	}

	@GET
	@Path("/users")
	@RolesAllowed("ADMIN")
	public List<User> getUsers() {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findAll( community );
	}

	@POST
	@RolesAllowed("ADMIN")
	@Path("/toggle-active")
	public void toggleActive( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		userDAO.toggleActive( community, email );
	}

}
