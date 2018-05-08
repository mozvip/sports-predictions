package predictions.model.db;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@RegisterRowMapper(CommunityMapper.class)
public interface CommunityDAO {
	
	@SqlUpdate("MERGE INTO COMMUNITY KEY(COMMUNITY_NAME) VALUES(:name, :createAccountEnabled, :openingDate, :groupsAccess, :finalsAccess)")
	void updateCommunity(@Bind("name") String name, @Bind("createAccountEnabled") boolean createAccountEnabled, @Bind("openingDate")ZonedDateTime openingDate, @Bind("groupsAccess") AccessType groupsAccess, @Bind("finalsAccess") AccessType finalsAccess);
	
	@SqlQuery("SELECT COMMUNITY.* FROM COMMUNITY WHERE COMMUNITY_NAME = :name")
	Community getCommunity(@Bind("name") String name);

}
