package predictions.model;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface CommunityDAO {
	
	@SqlUpdate("MERGE INTO COMMUNITY KEY(COMMUNITY_NAME) VALUES(:name, :createAccountEnabled, :groupsEditEnabled, :finalsEditEnabled)")
	public void updateCommunity(@Bind("name") String name, @Bind("createAccountEnabled") boolean createAccountEnabled, @Bind("groupsEditEnabled") boolean groupsEditEnabled, @Bind("finalsEditEnabled") boolean finalsEditEnabled);
	
	@SqlQuery("SELECT * FROM COMMUNITY WHERE COMMUNITY_NAME = :name")
	@Mapper(CommunityMapper.class)
	public Community getCommunity(@Bind("name") String name);

}
