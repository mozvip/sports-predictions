package predictions.views;

import io.dropwizard.views.View;

public class CreateUserView extends View {
	
	private String community;

	public CreateUserView( String community ) {
		super("create-user.ftl");
		this.community = community;
	}
	
	public String getCommunity() {
		return community;
	}

}
