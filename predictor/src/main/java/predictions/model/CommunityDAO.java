package predictions.model;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface CommunityDAO {
	
	@SqlUpdate("MERGE INTO COMMUNITY KEY(NAME) VALUES(:createAccountEnabled, :groupsEditEnabled, :finalsEditEnabled)")
	public void updateCommunity(@Bind("name") String name, @Bind("createAccountEnabled") boolean createAccountEnabled, @Bind("groupsEditEnabled") boolean groupsEditEnabled, @Bind("finalsEditEnabled") boolean finalsEditEnabled);

}
