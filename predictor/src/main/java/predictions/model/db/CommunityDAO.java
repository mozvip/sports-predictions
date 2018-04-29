package predictions.model.db;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface CommunityDAO {
	
	@SqlUpdate("MERGE INTO COMMUNITY KEY(COMMUNITY_NAME) VALUES(:name, :createAccountEnabled, :groupsAccess, :finalsAccess)")
	void updateCommunity(@Bind("name") String name, @Bind("createAccountEnabled") boolean createAccountEnabled, @Bind("groupsAccess") AccessType groupsAccess, @Bind("finalsAccess") AccessType finalsAccess);
	
	@SqlQuery("SELECT * FROM COMMUNITY WHERE COMMUNITY_NAME = :name")
	@Mapper(CommunityMapper.class)
	Community getCommunity(@Bind("name") String name);

}
