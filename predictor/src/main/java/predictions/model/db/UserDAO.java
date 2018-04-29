package predictions.model.db;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.LongMapper;

public interface UserDAO {

	@SqlUpdate("insert into user (email, name, community, password, admin, EMAIL_VALID, ACTIVE) values (:email, :name, :community, HASH('SHA256', STRINGTOUTF8(:password),1000), false, false, true)")
	long insert(@Bind("email") String email, @Bind("name") String name, @Bind("community") String community, @Bind("password") String password);

	@SqlUpdate("update user set CURRENT_SCORE=:score where community=:community AND email=:email")
	long updateScore(@Bind("email") String email, @Bind("community") String community, @Bind("score") int score);

	@SqlUpdate("update user set password = HASH('SHA256', STRINGTOUTF8(:password),1000) where email=:email and community=:community")
	long updatePassword(@Bind("email") String email, @Bind("community") String community, @Bind("password") String password);

	@SqlQuery("select * from user where community = :community AND active = true ORDER BY CURRENT_SCORE DESC")
	@Mapper(UserResultSetMapper.class)
	List<User> findUsersOrderedByScore(@Bind("community") String community);

	@SqlQuery("select * from user where community = :community AND admin = true AND active = true")
	@Mapper(UserResultSetMapper.class)
	List<User> findAdmins(@Bind("community") String community);
	
	@SqlQuery("select * from user where community = :community")
	@Mapper(UserResultSetMapper.class)
	List<User> findAll(@Bind("community") String community);

	@SqlQuery("select * from user where community = :community and email not in (select distinct email from match_prediction where community = :community)")
	@Mapper(UserResultSetMapper.class)
	List<User> findUsersWithNoPredictions(@Bind("community") String community);
	
	@SqlQuery("select * from user where community = :community and email = :email")
	@Mapper(UserResultSetMapper.class)
	User findUser(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user where community=:community and email=:email and password=HASH('SHA256', STRINGTOUTF8(:password),1000) AND active = true")
	@Mapper(UserResultSetMapper.class)
	User authentify(@Bind("community") String community, @Bind("email") String email, @Bind("password") String password);
	
	@SqlUpdate("update user set LAST_LOGIN_DATE = CURRENT_TIMESTAMP() where community=:community and email=:email")
	void updateLastLoginDate(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user where community=:community and email=LOWER(:email)")
	@Mapper(UserResultSetMapper.class)
	User findExistingUserByEmail(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user where community=:community and name=:name AND ACTIVE=true AND and email!=:email")
	@Mapper(UserResultSetMapper.class)
	User findExistingUserByName(@Bind("community") String community, @Bind("email") String email, @Bind("name") String name);

	@SqlUpdate("delete from user where community = :community and email=LOWER(:email)")
	void delete(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from user WHERE active = true")
	@Mapper(UserResultSetMapper.class)
	List<User> findAllActive();

	@SqlUpdate("update user set CHANGE_PASSWORD_TOKEN=:token where community=:community AND email=LOWER(:email)")
	void setChangePasswordToken(@Bind("community") String community, @Bind("email") String email, @Bind("token") UUID uuid);

	@SqlUpdate("update user set admin=:admin where community=:community AND email=LOWER(:email)")
	void setAdmin(@Bind("community") String community, @Bind("email") String email, @Bind("admin") boolean admin);

	@SqlUpdate("update user set active=NOT active where community=:community AND email=:email")
	void toggleActive(@Bind("community") String community, @Bind("email") String email);

	@SqlUpdate("update user set admin=NOT admin where community=:community AND email=:email")
	void toggleAdmin(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("SELECT COUNT(*) FROM USER WHERE ACTIVE=true AND COMMUNITY=:community")
	@Mapper(LongMapper.class)
	long getCount(@Bind("community") String community);

	@SqlUpdate("update user set name=:name where community=:community AND email=:email")
	void setName(@Bind("community") String community, @Bind("email") String email, @Bind("name") String name);
	
	@SqlUpdate("UPDATE user set CURRENT_SCORE = NVL(( SELECT SUM(SCORE) FROM MATCH_PREDICTION WHERE MATCH_PREDICTION.COMMUNITY = user.COMMUNITY and MATCH_PREDICTION.email = user.EMAIL), 0), PREVIOUS_RANKING = RANKING")
	void recalculateScores();
	
	@SqlUpdate("UPDATE USER SET RANKING = SELECT RANKING FROM ( SELECT a1.EMAIL, a1.COMMUNITY, a1.CURRENT_SCORE, COUNT (a2.CURRENT_SCORE) RANKING FROM USER a1, USER a2 WHERE (a1.CURRENT_SCORE < a2.CURRENT_SCORE AND A1.COMMUNITY = A2.COMMUNITY) OR (a1.CURRENT_SCORE=a2.CURRENT_SCORE AND a1.EMAIL = a2.EMAIL AND a1.COMMUNITY = a2.COMMUNITY) GROUP BY a1.EMAIL, A1.COMMUNITY, a1.CURRENT_SCORE ORDER BY a1.CURRENT_SCORE DESC, a1.EMAIL DESC) WHERE EMAIL = USER.EMAIL AND COMMUNITY = USER.COMMUNITY;")
	void updateRankings();

	@SqlUpdate("INSERT INTO USER_RANKINGS(COMMUNITY, EMAIL, GAME_NUM, GAME_DATE, RANKING) VALUES (:community, :email, :gameNum, :gameDate, :ranking)")
	void insertRankings(@Bind("community") String community, @Bind("email") String email, @Bind("gameNum") String gameNum, @Bind("gameDate") Date gameDate, @Bind("ranking") int ranking);

}
