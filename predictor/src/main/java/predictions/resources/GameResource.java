package predictions.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import predictions.model.GameStats;
import predictions.model.db.MatchPrediction;
import predictions.model.db.MatchPredictionDAO;
import predictions.model.db.User;

@Path("/game")
@Produces(MediaType.APPLICATION_JSON)
@Api
public class GameResource {
	
	@Context private HttpServletRequest httpRequest;
	
	private MatchPredictionDAO gamePredictionsDAO;
	
	public GameResource( MatchPredictionDAO dao ) {
		this.gamePredictionsDAO = dao;
	}
	
	@GET
	@ApiOperation(tags="user", value="Retrieve the statistics for a game", authorizations = @Authorization("basicAuth"))
	public GameStats getGameStats(@ApiParam(hidden = true) @Auth User user, @NotNull @QueryParam("gameId") int gameId) {
		
		String community = user.getCommunity();

		List<MatchPrediction> predictionsForGame = gamePredictionsDAO.findByGame( community,  gameId);
		
		int perfect = 0;
		int good = 0;
		int bad = 0;

		for (MatchPrediction matchPrediction : predictionsForGame) {

			//FIXME
			if (matchPrediction.getMatch_id() < 36 ) {
				
				if (matchPrediction.getScore() == 3) {
					perfect ++;
				} else if (community.equals("michelin-solutions") && matchPrediction.getScore() == 2) {
					good ++;
				} else if (!community.equals("michelin-solutions") && matchPrediction.getScore() == 1) {
					good ++;
				} else {
					bad ++;
				}

			} else {	// finals phase
				
				if (matchPrediction.getScore() == 5) {
					perfect ++;
				} else if (matchPrediction.getScore() == 3) {
					good ++;
				} else {
					bad ++;
				}				
				
			}
			
		}
		
		return new GameStats( predictionsForGame.get(0).getHome_team_id(), predictionsForGame.get(0).getAway_team_id(), predictionsForGame.size(), perfect, good, bad);
		
	}

}
