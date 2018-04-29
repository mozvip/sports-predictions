package predictions.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Community {

	@JsonProperty
	private String name;

	@JsonProperty
	private boolean createAccountEnabled = true;

	@JsonProperty
	private AccessType groupsAccess = AccessType.N;

	@JsonProperty
	private AccessType finalsAccess = AccessType.N;

	public Community() {
		
	}

	public Community(String name, boolean createAccountEnabled, AccessType groupsAccess, AccessType finalsAccess) {
		super();
		this.name = name;
		this.createAccountEnabled = createAccountEnabled;
		this.groupsAccess = groupsAccess;
		this.finalsAccess = finalsAccess;
	}

	public String getName() {
		return name;
	}

	public boolean isCreateAccountEnabled() {
		return createAccountEnabled;
	}

	public AccessType getGroupsAccess() {
		return groupsAccess;
	}
	
	public AccessType getFinalsAccess() {
		return finalsAccess;
	}

}
