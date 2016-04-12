package predictions.views;

import java.util.List;

import io.dropwizard.views.View;
import predictions.model.ActualResult;
import predictions.model.ActualResultDAO;
import predictions.model.Game;

public class SubmitScoreView extends View {
	
	private List<Game> games;
	private ActualResultDAO actualResultDAO;

	public SubmitScoreView( List<Game> games, ActualResultDAO actualResultDAO ) {
		super("submit-score.ftl");
		this.games = games;
		this.actualResultDAO = actualResultDAO;
	}
	
	public List<Game> getGames() {
		return games;
	}

	public List<ActualResult> getMatches() {
		return actualResultDAO.findAll();
	}

}
