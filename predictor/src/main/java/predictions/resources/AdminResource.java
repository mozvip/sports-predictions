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

import io.swagger.annotations.Api;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Api
public class AdminResource {

	private UserDAO userDAO;

	@Context private HttpServletRequest httpRequest;

	public AdminResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@GET
	@Path("/users")
	@RolesAllowed("ADMIN")
	public List<User> getUsers() {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findAll( community );
	}

	@GET
	@Path("/users-no-prediction")
	@RolesAllowed("ADMIN")
	public List<User> getUsersWithNoPredictions() {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findUsersWithNoPredictions( community );
	}

	@POST
	@Path("/toggle-active")
	@RolesAllowed("ADMIN")
	public void toggleActive( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		userDAO.toggleActive( community, email );
	}

	@POST
	@Path("/delete-user")
	@RolesAllowed("ADMIN")
	public void deleteUser( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		userDAO.delete( community, email );
	}

	@POST
	@Path("/toggle-admin")
	@RolesAllowed("ADMIN")
	public void toggleAdmin( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		userDAO.toggleAdmin( community, email );
	}

}
