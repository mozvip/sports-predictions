package predictions.resources;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.Min;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.model.ActualResult;
import predictions.model.ActualResultDAO;
import predictions.model.Game;
import predictions.model.MatchPrediction;
import predictions.model.MatchPredictionDAO;
import predictions.model.User;
import predictions.model.UserDAO;

@Path("/score")
@Produces(MediaType.APPLICATION_JSON)
public class ScoreResource {
	
	private final static Logger logger = LoggerFactory.getLogger( ScoreResource.class );
	
	ActualResultDAO actualResultDAO;
	MatchPredictionDAO matchPredictionDAO;
	UserDAO userDAO;
	
	private List<Game> games;
	private Map<Integer, Game> gamesById = new HashMap<>();
	private Map<Integer, String> winners = new HashMap<>();
	
	public ScoreResource(ActualResultDAO actualResultDAO, MatchPredictionDAO matchPredictionDAO, UserDAO userDAO ) {
		this.actualResultDAO = actualResultDAO;
		this.matchPredictionDAO = matchPredictionDAO;
		this.userDAO = userDAO;
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("games.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			games = mapper.readValue(input, new TypeReference<List<Game>>(){});
			for (Game game : games) {
				gamesById.put(game.getMatchNum(), game);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		associateScores();
		
	}
	
	private synchronized void associateScores() {
		List<ActualResult> allResults = actualResultDAO.findAll();
		for (ActualResult actualResult : allResults) {
			int gameId = actualResult.getMatch_id();
			Game game = gamesById.get( gameId );
			game.setDone( true );
			game.setHomeScore( actualResult.getHome_score());
			game.setAwayScore( actualResult.getAway_score());
			game.setWinningTeam( actualResult.isHome_winner() ? actualResult.getHome_team_name() : actualResult.getAway_team_name() );
			
			winners.put(game.getMatchNum(), game.getWinningTeam());
		}
		
		for (Game game : games) {
			if (game.getHomeTeam() == null || game.getAwayTeam() == null) {
				game.setHomeTeam( winners.get( game.getHomeTeamWinnerFrom() ));
				game.setAwayTeam( winners.get( game.getAwayTeamWinnerFrom() ));
			}
		}
	}
	
	@GET
	@Path("/games")
	@ApiOperation(value="Get the list of all games, along with the final result for the games which have already been played")
	public List<Game> getGames() {
		return games;
	}
	
	private synchronized void updateMatchPredictionScores( int gameNum, String homeTeam, String awayTeam, int homeScore, int awayScore, boolean homeWinning ) {

		List<MatchPrediction> predictions = matchPredictionDAO.findPredictions( gameNum );
		for (MatchPrediction prediction : predictions) {
			
			int matchScore = 0;
			
			if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore && prediction.isHome_winner() == homeWinning) {
				
				// perfect score
				matchScore = 3;
				
			} else if (
					( prediction.getHome_score() == prediction.getAway_score() && homeScore == awayScore ) ||
					( prediction.getHome_score() > prediction.getAway_score() && homeScore > awayScore ) ||
					( prediction.getHome_score() < prediction.getAway_score() && homeScore < awayScore ) )
			{
				
				if ( prediction.getCommunity().startsWith("michelin-solutions")) {
					matchScore = 2;
				} else {
					matchScore = 1;
				}
				
			} else if ( homeScore == awayScore && prediction.isHome_winner() == homeWinning) {
				
				if ( prediction.getCommunity().startsWith("michelin-solutions")) {
					matchScore = 2;
				} else {
					matchScore = 1;
				}
				
			} else if ( prediction.getHome_score() == homeScore || prediction.getAway_score() == awayScore ) {
				if ( prediction.getCommunity().startsWith("michelin-solutions")) {
					matchScore = 1;
				} else {
					matchScore = 0;
				}
			}
			
			matchPredictionDAO.updateScore( prediction.getCommunity(), prediction.getEmail(), prediction.getMatch_id(), matchScore);
		}
		
		userDAO.recalculateScores();
		userDAO.updateRankings();

	}

	@RolesAllowed("ADMIN")
	@Path("/submit")
	@POST
	@ApiOperation(value="Admin users can call this API to submit actual scores after a game ended, this will recalculate scores and rankings, winningTeamName must be specified only is not obvious from the score (penalty shootout was used to determine the winner)")
	public Response postScore( @Auth User user, @Min(0) @FormParam("gameNum") int gameNum, @Min(0) @FormParam("homeScore") int homeScore, @Min(0) @FormParam("awayScore") int awayScore, @FormParam("winningTeamName") String winningTeamName) {

		Game game = gamesById.get( gameNum );
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

		boolean homeWinning = winningTeamName != null ? winningTeamName.equals( game.getHomeTeam()) : homeScore > awayScore;
		actualResultDAO.merge(gameNum, homeScore, awayScore, game.getHomeTeam(), game.getAwayTeam(), homeWinning );
		updateMatchPredictionScores( gameNum, "", "", homeScore, awayScore, homeWinning );
		associateScores();
		
		return Response.ok().build();
	}

}
