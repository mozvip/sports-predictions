package predictions.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import predictions.model.Game;
import predictions.model.GamesManager;
import predictions.model.db.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Api(tags="admin", authorizations = @Authorization("basicAuth"))
public class AdminResource {

	private UserDAO userDAO;
	private MatchPredictionDAO matchPredictionDAO;
	private ActualResultDAO actualResultDAO;

	private GamesManager gamesManager;

	@Context private HttpServletRequest httpRequest;

	public AdminResource(UserDAO userDAO, MatchPredictionDAO matchPredictionDAO, ActualResultDAO actualResultDAO, GamesManager gamesManager) {
		this.userDAO = userDAO;
		this.matchPredictionDAO = matchPredictionDAO;
		this.actualResultDAO = actualResultDAO;
		this.gamesManager = gamesManager;
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

	@POST
	@Path("/toggle-late/{email}")
	@ApiOperation(value="Toggle the late status of an user of this community")
	public void toggleLate(@NotNull @PathParam("email") String email) {
		String community = getCommunity();
		email = email.toLowerCase().trim();
		userDAO.toggleLate( community, email );
	}

	@POST
	@Path("/submit-prediction")
	@RolesAllowed("ADMIN")
	@ApiOperation(tags="admin", value="Submit a single prediction for a user", authorizations = @Authorization("basicAuth"))
	public Response adminSaveSinglePrediction(
			@NotNull @FormParam("email") String email,
			@NotNull @FormParam("gameId") int gameId,
			@NotNull @FormParam("homeScore") int homeScore,
			@NotNull @FormParam("awayScore") int awayScore,
			@FormParam("homeTeam") String homeTeam,
			@FormParam("awayTeam") String awayTeam,
			@FormParam("homeWinner") boolean homeWinner
	) {
		String communityName = getCommunity();
		Game game = gamesManager.getGame(gameId);
		if (game == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		User user = userDAO.findUser(communityName, email);
		if (user == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if (game.getGroup() != null) {
			matchPredictionDAO.merge(communityName, email, gameId, homeScore, awayScore, game.getHomeTeamName(), game.getAwayTeamName(), false);
		} else {
			matchPredictionDAO.merge(communityName, email, gameId, homeScore, awayScore, homeTeam, awayTeam, homeWinner);
		}

		ActualResult actualResult = actualResultDAO.find(gameId);
		if (actualResult != null) {
			gamesManager.refreshScore(gameId);
		}

		return Response.ok().build();
	}


}
