package predictions.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
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
	public List<User> getUsers() {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findAll( community );
	}
	
}
