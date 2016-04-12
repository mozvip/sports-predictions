package predictions.views;

import io.dropwizard.views.View;
import predictions.model.User;
import predictions.model.UserDAO;

import java.util.List;

public class UserListView extends View {
	
	private String community;
	private UserDAO userDAO;

	public UserListView(UserDAO userDAO, String community) {
		super("user-list.ftl");
		this.userDAO = userDAO;
		this.community = community;
	}
	
	public List<User> getUsers() {
		return userDAO.findUsersOrderedByScore(community);
	}

}
