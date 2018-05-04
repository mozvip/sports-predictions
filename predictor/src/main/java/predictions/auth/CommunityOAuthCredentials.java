package predictions.auth;

public class CommunityOAuthCredentials {

	private String community;
	private String token;

	public CommunityOAuthCredentials(String community, String token) {
		this.community = community;
		this.token = token;
	}
	
	public String getCommunity() {
		return community;
	}

	public String getToken() {
		return token;
	}
}
