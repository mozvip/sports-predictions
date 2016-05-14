package predictions.resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
import predictions.views.SubmitScoreView;

@Path("/score")
@Produces("text/html; charset=UTF-8")
public class ScoreResource {
	
	private final static Logger logger = LoggerFactory.getLogger( ScoreResource.class );
	
	ActualResultDAO actualResultDAO;
	MatchPredictionDAO matchPredictionDAO;
	UserDAO userDAO;
	
	private List<Game> games;
	private Map<Integer, Game> gamesById = new HashMap<>();
	
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
	
	private void associateScores() {
		List<ActualResult> allResults = actualResultDAO.findAll();
		for (ActualResult actualResult : allResults) {
			int gameId = actualResult.getMatch_id();
			Game game = gamesById.get( gameId );
			game.setDone( true );
			game.setHomeScore( actualResult.getHome_score());
			game.setAwayScore( actualResult.getAway_score());
			game.setWinningTeam( actualResult.isHome_winner() ? actualResult.getHome_team_name() : actualResult.getAway_team_name() );
		}
	}
	
	@GET
	@Path("/games")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Get the list of all games, along with the final result for the games which have already been played")
	public List<Game> getGames() {
		return games;
	}
	
	private void recalculateScores() {
		
		Map<String, Integer> scores = new HashMap<String, Integer>();
		
		List<User> allUsers = userDAO.findUsers();
		for (User user : allUsers) {
			scores.put( String.format("%s:%s", user.getCommunity(), user.getEmail().toLowerCase()), 0 );
		}

		List<MatchPrediction> predictions = matchPredictionDAO.findAll();
		List<ActualResult> results = actualResultDAO.findValidated();
		for (ActualResult actualResult : results) {
			for (MatchPrediction prediction : predictions) {
				
				String ident = String.format("%s:%s", prediction.getCommunity(), prediction.getEmail().toLowerCase());
				
				if (!scores.containsKey( ident )) {
					continue;
				}
				
				if (prediction.getMatch_id() == actualResult.getMatch_id()) {
					
					int matchScore = 0;
					
					if ( prediction.getHome_score() == actualResult.getHome_score() && prediction.getAway_score() == actualResult.getAway_score() ) {
					
						matchScore = 3;
					
					} else if (
							( prediction.getHome_score() == prediction.getAway_score() && actualResult.getHome_score() == actualResult.getAway_score() ) ||
							( prediction.getHome_score() > prediction.getAway_score() && actualResult.getHome_score() > actualResult.getAway_score() ) ||
							( prediction.getHome_score() < prediction.getAway_score() && actualResult.getHome_score() < actualResult.getAway_score() ) )
					{
						
						matchScore = 2;
						
					} else if ( prediction.getHome_score() == actualResult.getHome_score() || prediction.getAway_score() == actualResult.getAway_score() ) {
						if ( prediction.getCommunity().startsWith("michelin")) {
							matchScore = 1;
						}
					}
					
					scores.put( ident, scores.get( ident ) + matchScore );
				}
			}
		}
		
		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			String ident = entry.getKey();
			int index = ident.indexOf(':');
			String community = ident.substring( 0, index );
			String email = ident.substring( index + 1 );
			userDAO.updateScore( email, community, entry.getValue() );
		}
		
	}
	
	@RolesAllowed("ADMIN")
	@Path("/submit")
	@POST
	@ApiOperation(value="Admin users can call this API to submit actual scores after a game ended, this will recalculate all scores, winningTeamName must be specified only is not obvious from the score (penalty shootout was used to determine the winner)")
	public void postScore( @Auth User user, @FormParam("gameNum") int gameNum, @FormParam("homeScore") int homeScore, @FormParam("awayScore") int awayScore, @FormParam("winningTeamName") String winningTeamName) {
		ActualResult result = actualResultDAO.find(gameNum);
		boolean homeWinning = winningTeamName != null ? winningTeamName.equals( result.getHome_team_name()) : homeScore > awayScore;
		actualResultDAO.insert(gameNum, homeScore, awayScore, result.getHome_team_id(), result.getAway_team_id(), homeWinning );
		recalculateScores();
		associateScores();
	}

	@RolesAllowed("ADMIN")
	@Path("/submit")
	@Produces("text/html; charset=UTF-8")
	@GET
	@ApiOperation(value="Displays the admin view to submit scores", hidden=true)
	public SubmitScoreView get() {
		return new SubmitScoreView( games, actualResultDAO );
	}

}
