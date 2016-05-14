package predictions.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.ApiOperation;
import predictions.model.UserDAO;
import predictions.views.RankingsView;

@Path("/rankings")
@Produces(MediaType.TEXT_HTML)
public class RankingsResource {
	
	private UserDAO userDAO;
	
	@Context private HttpServletRequest httpRequest;

	public RankingsResource(UserDAO userDAO) {
		super();
		this.userDAO = userDAO;
	}

	@GET
	@ApiOperation(value="Displays the rankings view", hidden=true)
	public RankingsView getView() {
		return new RankingsView( userDAO, (String) httpRequest.getAttribute("community") );
	}

}
