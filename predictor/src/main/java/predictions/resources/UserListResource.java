package predictions.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.ApiOperation;
import predictions.model.MatchPredictionDAO;
import predictions.model.UserDAO;
import predictions.views.UserListView;

@Path("/user-list")
@Produces(MediaType.TEXT_HTML)
public class UserListResource {
	
    private UserDAO userDAO;
    private MatchPredictionDAO matchPredictionDAO;
    
    @Context private HttpServletRequest httpRequest;
	
	public UserListResource(UserDAO userDAO,
			MatchPredictionDAO matchPredictionDAO) {
		this.userDAO = userDAO;
		this.matchPredictionDAO = matchPredictionDAO;
	}

	@GET
	@ApiOperation(value="Display the list of users view", hidden=true)
	public UserListView getUserList() {
		String community = (String) httpRequest.getAttribute("community");
		return new UserListView( userDAO, community );
	}

}
