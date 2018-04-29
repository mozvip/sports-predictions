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

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
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
			
			String winningTeam = actualResult.getHome_team_id();
			if (actualResult.getHome_score() > actualResult.getAway_score()) {
				winningTeam = actualResult.getHome_team_id();
			} else if (actualResult.getHome_score() < actualResult.getAway_score()) {
				winningTeam = actualResult.getAway_team_id();
			} else {
				winningTeam = actualResult.isHome_winner() ? actualResult.getHome_team_id() : actualResult.getAway_team_id();
			}

			game.setWinningTeam( winningTeam );
			
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
	
	@GET
	@Path("/recalculate")
	@RolesAllowed("ADMIN")
	@ApiOperation(value="Recalculate all scores")
	public void recalculate() {
		List<ActualResult> allResults = actualResultDAO.findAll();
		for (ActualResult actualResult : allResults) {
			updateMatchPredictionScores(actualResult.getMatch_id(), actualResult.getHome_team_id(), actualResult.getAway_team_id(), actualResult.getHome_score(), actualResult.getAway_score(), actualResult.isHome_winner());
		}
		userDAO.recalculateScores();
		userDAO.updateRankings();
	}
	
	private synchronized void updateMatchPredictionScores( int gameNum, String homeTeam, String awayTeam, int homeScore, int awayScore, boolean homeWinning ) {
		
		Game game = games.get( gameNum );
		
		String actualWinner = awayTeam;
		if (homeScore > awayScore) {
			actualWinner = homeTeam;
		} else if (awayScore > homeScore) {
			actualWinner = awayTeam;
		} else {
			actualWinner = homeWinning ? homeTeam : awayTeam;
		}

		List<MatchPrediction> predictions = matchPredictionDAO.findPredictions( gameNum );
		for (MatchPrediction prediction : predictions) {
			
			String predictionWinner = prediction.getAway_team_id();
			if (prediction.getHome_score() > prediction.getAway_score()) {
				predictionWinner = prediction.getHome_team_id();
			} else if (prediction.getAway_score() > prediction.getHome_score()) {
				predictionWinner = prediction.getAway_team_id();
			} else {
				predictionWinner = prediction.isHome_winner() ? prediction.getHome_team_id() : prediction.getAway_team_id();
			}
			
			int matchScore = 0;
			
			if ( prediction.getCommunity().startsWith("michelin-solutions")) {
				

				if (!game.getGroup().startsWith("Groupe ")) {
					
					if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore && prediction.getHome_team_id().equals(game.getHomeTeam()) && prediction.getAway_team_id().equals(game.getAwayTeam())) {
						matchScore = 5;
						if (homeScore == awayScore) { // draw
							if (prediction.isHome_winner() != homeWinning) { // wrong qualified team
								matchScore = 2;
							}
						}
					} else if ( actualWinner.equals( predictionWinner )) {
						matchScore = 3;
					} else if ((prediction.getHome_score() == homeScore && prediction.getHome_team_id().equals(game.getHomeTeam())) || (prediction.getAway_score() == awayScore && prediction.getAway_team_id().equals(game.getAwayTeam()))) {
						matchScore = 1;
					}
					
				} else {
					
					if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore) {
						// perfect score
						matchScore = 3;
					} else if (
							( prediction.getHome_score() == prediction.getAway_score() && homeScore == awayScore ) ||
							( prediction.getHome_score() > prediction.getAway_score() && homeScore > awayScore ) ||
							( prediction.getHome_score() < prediction.getAway_score() && homeScore < awayScore ) )
					{
						
						matchScore = 2;

					} else if ((prediction.getHome_score() == homeScore && prediction.getHome_team_id().equals(game.getHomeTeam())) || (prediction.getAway_score() == awayScore && prediction.getAway_team_id().equals(game.getAwayTeam()))) {
						matchScore = 1;
					}
					
				}
				
				
			} else {
				

				if (!game.getGroup().startsWith("Groupe ")) {
					
					if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore && prediction.getHome_team_id().equals(game.getHomeTeam()) && prediction.getAway_team_id().equals(game.getAwayTeam())) {
						matchScore = 5;
						if (homeScore == awayScore) { // draw
							if (prediction.isHome_winner() != homeWinning) { // wrong qualified team
								matchScore = 1;
							}
						}
					} else if ( actualWinner.equals( predictionWinner )) {
						matchScore = 3;
					}
					
				} else {
					
					if ( prediction.getHome_score() == homeScore && prediction.getAway_score() == awayScore) {
						// perfect score
						matchScore = 3;
					} else if (
							( prediction.getHome_score() == prediction.getAway_score() && homeScore == awayScore ) ||
							( prediction.getHome_score() > prediction.getAway_score() && homeScore > awayScore ) ||
							( prediction.getHome_score() < prediction.getAway_score() && homeScore < awayScore ) )
					{
						
						matchScore = 1;
					}
					
				}

			}
			
			matchPredictionDAO.updateScore( prediction.getCommunity(), prediction.getEmail(), prediction.getMatch_id(), matchScore);
		}
		
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

		boolean homeWinning = false;
		if (homeScore > awayScore) {
			homeWinning = true;
		} else if (awayScore > homeScore) {
			homeWinning = false;
		} else {
			homeWinning = winningTeamName != null ? winningTeamName.equals( game.getHomeTeam()) : false;
		}
		actualResultDAO.merge(gameNum, homeScore, awayScore, game.getHomeTeam(), game.getAwayTeam(), homeWinning );
		
		updateMatchPredictionScores( gameNum, game.getHomeTeam(), game.getAwayTeam(), homeScore, awayScore, homeWinning );
		userDAO.recalculateScores();
		userDAO.updateRankings();
		associateScores();
		
		return Response.ok().build();
	}

}
