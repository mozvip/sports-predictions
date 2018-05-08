package predictions.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Community {

	@JsonProperty
	private String name;

	@JsonProperty
	private boolean createAccountEnabled = true;

	@JsonProperty
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private ZonedDateTime openingDate = null;

	@JsonProperty
	private AccessType groupsAccess = AccessType.N;

	@JsonProperty
	private AccessType finalsAccess = AccessType.N;

	public Community() {
		
	}

	public Community(String name, boolean createAccountEnabled, ZonedDateTime openingDate, AccessType groupsAccess, AccessType finalsAccess) {
		super();
		this.name = name;
		this.openingDate = openingDate;
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

	public ZonedDateTime getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(ZonedDateTime openingDate) {
		this.openingDate = openingDate;
	}
}
