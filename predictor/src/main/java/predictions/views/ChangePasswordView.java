package predictions.views;

import io.dropwizard.views.View;
import predictions.model.UserDAO;

public class ChangePasswordView extends View {
	
	private String community;
	private UserDAO userDAO;

	public ChangePasswordView(UserDAO userDAO, String community) {
		super("change-password.ftl");
		this.userDAO = userDAO;
		this.community = community;
	}
	
	public String getCommunity() {
		return community;
	}
	
	public UserDAO getUserDAO() {
		return userDAO;
	}
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

}
