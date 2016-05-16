package predictions.model;

import java.util.List;
import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface UserDAO {

	@SqlUpdate("insert into user (email, name, community, password, admin, EMAIL_VALID) values (:email, :name, :community, HASH('SHA256', STRINGTOUTF8(:password),1000), false, false)")
	long insert(@Bind("email") String email, @Bind("name") String name, @Bind("community") String community, @Bind("password") String password);

	@SqlUpdate("update user set CURRENT_SCORE=:score where community=:community AND email=LOWER(:email)")
	long updateScore(@Bind("email") String email, @Bind("community") String community, @Bind("score") int score);

	@SqlUpdate("update user set password = HASH('SHA256', STRINGTOUTF8(:password),1000) where email=:email and community=:community")
	long updatePassword(@Bind("email") String email, @Bind("community") String community, @Bind("password") String password);

	@SqlQuery("select * from user where community = :community ORDER BY CURRENT_SCORE DESC")
	@Mapper(UserResultSetMapper.class)
	List<User> findUsersOrderedByScore(@Bind("community") String community);

	@SqlQuery("select * from user where community = :community AND admin = true")
	@Mapper(UserResultSetMapper.class)
	List<User> findAdmins(@Bind("community") String community);
	
	@SqlQuery("select * from user where community=:community and email=:email and password=HASH('SHA256', STRINGTOUTF8(:password),1000)")
	@Mapper(UserResultSetMapper.class)
	User authentify(@Bind("community") String community, @Bind("email") String email, @Bind("password") String password);
	
	@SqlUpdate("update user set LAST_LOGIN_DATE = CURRENT_TIMESTAMP() where community=:community and email=:email")
	void updateLastLoginDate(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user where community=:community and email=LOWER(:email)")
	@Mapper(UserResultSetMapper.class)
	User findExistingUser(@Bind("community") String community, @Bind("email") String email);

	@SqlUpdate("delete from user where community = :community and email=LOWER(:email)")
	void delete(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user")
	@Mapper(UserResultSetMapper.class)
	List<User> findUsers();

	@SqlUpdate("update user set CHANGE_PASSWORD_TOKEN=:token where community=:community AND email=LOWER(:email)")
	void setChangePasswordToken(@Bind("community") String community, @Bind("email") String email, @Bind("token") UUID uuid);

	@SqlUpdate("update user set admin=:admin where community=:community AND email=LOWER(:email)")
	void setAdmin(@Bind("community") String community, @Bind("email") String email, @Bind("admin") boolean admin);

}
