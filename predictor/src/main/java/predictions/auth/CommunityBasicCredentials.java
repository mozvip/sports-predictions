package predictions.auth;

import io.dropwizard.auth.basic.BasicCredentials;

public class CommunityBasicCredentials extends BasicCredentials {
	
	private String community;

	public CommunityBasicCredentials(String community, String username, String password) {
		super(username, password);
		this.community = community;
	}
	
	public String getCommunity() {
		return community;
	}

}
