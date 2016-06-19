package predictions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Community {

	@JsonProperty
	private String name;

	@JsonProperty
	private boolean createAccountEnabled = true;

	@JsonProperty
	private boolean groupsEditEnabled = true;

	@JsonProperty
	private boolean finalsEditEnabled = true;
	
	public Community() {
		
	}

	public Community(String name, boolean createAccountEnabled, boolean groupsEditEnabled, boolean finalsEditEnabled) {
		super();
		this.name = name;
		this.createAccountEnabled = createAccountEnabled;
		this.groupsEditEnabled = groupsEditEnabled;
		this.finalsEditEnabled = finalsEditEnabled;
	}

	public String getName() {
		return name;
	}

	public boolean isCreateAccountEnabled() {
		return createAccountEnabled;
	}

	public boolean isGroupsEditEnabled() {
		return groupsEditEnabled;
	}

	public boolean isFinalsEditEnabled() {
		return finalsEditEnabled;
	}

}
