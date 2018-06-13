package predictions.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Api(tags="admin", authorizations = @Authorization("basicAuth"))
public class AdminResource {

	private UserDAO userDAO;

	@Context private HttpServletRequest httpRequest;

	public AdminResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	protected String getCommunity() {
		return httpRequest != null ? (String) httpRequest.getAttribute("community") : null;
	}

	@GET
	@Path("/users")
	@ApiOperation(value="Get all users of the community")
	public List<User> getUsers() {
		String community = getCommunity();
		return userDAO.findAll( community );
	}

	@GET
	@Path("/users-no-prediction")
	@ApiOperation(value="Get all users of the community who did not make their predictions")
	public List<User> getUsersWithNoPredictions() {
		String community = getCommunity();
		return userDAO.findUsersWithNoPredictions( community );
	}

	@POST
	@Path("/toggle-active/{email}")
	@ApiOperation(value="Toggle the active state of an user of this community")
	public void toggleActive(@NotNull @PathParam("email") String email) {
		String community = getCommunity();
		email = email.toLowerCase().trim();
		userDAO.toggleActive( community, email );
	}

	@POST
	@Path("/toggle-admin/{email}")
	@ApiOperation(value="Toggle the admin status of an user of this community")
	public void toggleAdmin(@NotNull @PathParam("email") String email) {
		String community = getCommunity();
		email = email.toLowerCase().trim();
		userDAO.toggleAdmin( community, email );
	}

}
