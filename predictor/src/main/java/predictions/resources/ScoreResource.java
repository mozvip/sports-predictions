package predictions.resources;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.model.GamesManager;
import predictions.model.db.ActualResult;
import predictions.model.db.ActualResultDAO;
import predictions.model.Game;
import predictions.model.db.MatchPrediction;
import predictions.model.db.MatchPredictionDAO;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

@Path("/score")
@Produces(MediaType.APPLICATION_JSON)
@Api
public class ScoreResource {
	
	private final static Logger logger = LoggerFactory.getLogger( ScoreResource.class );
	
	ActualResultDAO actualResultDAO;
	MatchPredictionDAO matchPredictionDAO;
	UserDAO userDAO;

	private GamesManager gamesManager;

	private Map<Integer, String> winners = new HashMap<>();
	
	public ScoreResource(GamesManager gamesManager, ActualResultDAO actualResultDAO, MatchPredictionDAO matchPredictionDAO, UserDAO userDAO ) throws IOException {
		this.gamesManager = gamesManager;
		this.actualResultDAO = actualResultDAO;
		this.matchPredictionDAO = matchPredictionDAO;
		this.userDAO = userDAO;
	}
	
	@GET
	@Path("/games")
	@ApiOperation(tags="public", value="Get the list of all games, along with the final result for the games which have already been played")
	public List<Game> getGames() {
		return gamesManager.getGames();
	}
	
	@GET
	@Path("/recalculate")
	@RolesAllowed("ADMIN")
	@ApiOperation(tags="admin", value="Recalculate all scores", authorizations = @Authorization("basicAuth"))
	public void recalculate() {
		List<ActualResult> allResults = actualResultDAO.findAll();
		for (ActualResult actualResult : allResults) {
			gamesManager.refreshScore(actualResult.getMatch_id(), actualResult);
		}
		userDAO.recalculateScores();
		userDAO.updateRankings();
	}

	@RolesAllowed("ADMIN")
	@Path("/submit")
	@POST
	@ApiOperation(tags="admin", value="Submit actual score for a game after it ended, this will recalculate scores and rankings, winningTeamName must be specified only is not obvious from the score (penalty shootout was used to determine the winner)", authorizations = @Authorization("basicAuth"))
	public Response postScore(@ApiParam(hidden = true) @Auth User user, @Min(0) @FormParam("gameNum") int gameNum, @Min(0) @FormParam("homeScore") int homeScore, @Min(0) @FormParam("awayScore") int awayScore, @FormParam("winningTeamName") String winningTeamName) {

		Game game = gamesManager.getGame(gameNum);
		if (game == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
		if (game.getDateTime().after( new Date())) {
			// game has not started yet !!
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		
		if (game.isDone()) {
			// score was already submitted 
			// do nothing : might be a data fix ??? this will break the previous ranking in this case : minor issue
		}

		boolean homeWinning;
		if (homeScore > awayScore) {
			homeWinning = true;
		} else if (awayScore > homeScore) {
			homeWinning = false;
		} else {
			homeWinning = winningTeamName != null ? winningTeamName.equals( game.getHomeTeamName()) : false;
		}
		actualResultDAO.merge(gameNum, homeScore, awayScore, game.getHomeTeamName(), game.getAwayTeamName(), homeWinning );
		gamesManager.refreshScore(gameNum);
		userDAO.recalculateScores();
		userDAO.updateRankings();

		gamesManager.setFinalScore(gameNum, homeScore, awayScore, homeWinning);
		
		return Response.ok().build();
	}

}
