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
	
	ActualResultDAO actualResultDAO;
	MatchPredictionDAO matchPredictionDAO;
	UserDAO userDAO;
	
	private List<Game> games;
	
	public ScoreResource(ActualResultDAO actualResultDAO, MatchPredictionDAO matchPredictionDAO, UserDAO userDAO ) {
		this.actualResultDAO = actualResultDAO;
		this.matchPredictionDAO = matchPredictionDAO;
		this.userDAO = userDAO;
		
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("games.json");
		ObjectMapper mapper = new ObjectMapper();
		try {
			games = mapper.readValue(input, new TypeReference<List<Game>>(){});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	@ApiOperation(value="Admin users can call this API to submit actual scores after a game ended.")
	public void postScore( @Auth User user, @FormParam("gameNum") int gameNum, @FormParam("homeScore") int homeScore, @FormParam("awayScore") int awayScore) {
		ActualResult result = actualResultDAO.find(gameNum);
		actualResultDAO.insert(gameNum, homeScore, awayScore, result.getHome_team_id(), result.getAway_team_id(), homeScore > awayScore ? true : false );
		recalculateScores();
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
