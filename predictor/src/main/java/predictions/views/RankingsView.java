package predictions.views;

import java.util.List;

import io.dropwizard.views.View;
import predictions.model.User;
import predictions.model.UserDAO;

public class RankingsView extends View {
	
	private String community;
	private UserDAO userDAO;

	public RankingsView( UserDAO userDAO, String community) {
		super("rankings.ftl");
		this.community = community;
		this.userDAO = userDAO;
	}
	
	public String getCommunity() {
		return community;
	}
	
	public List<User> getUsers() {
		return userDAO.findUsersOrderedByScore(community);
	}

}
