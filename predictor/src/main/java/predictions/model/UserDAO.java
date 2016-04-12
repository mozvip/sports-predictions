package predictions.model;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface UserDAO {

	  @SqlUpdate("insert into user (email, name, community, password, admin) values (:email, :name, :community, HASH('SHA256', STRINGTOUTF8(:password),1000), :admin)")
	  void insert(@Bind("email") String email, @Bind("name") String name, @Bind("community") String community, @Bind("password") String password, @Bind("admin") boolean admin);

	  @SqlUpdate("update user set currentScore=:score where community=:community AND UPPER(email)=UPPER(:email)")
	  void updateScore(@Bind("email") String email, @Bind("community") String community, @Bind("score") int score);

	  @SqlUpdate("update user set password = HASH('SHA256', STRINGTOUTF8(:password),1000) where UPPER(email)=:email and community=:community")
	  void updatePassword(@Bind("email") String email, @Bind("community") String community, @Bind("password") String password);

	  @SqlQuery("select * from user where community = :community ORDER BY currentScore DESC")
	  @Mapper(UserResultSetMapper.class)
	  List<User> findUsersOrderedByScore(@Bind("community") String community);

	  @SqlQuery("select * from user where community=:community and UPPER(email)=:email and password=HASH('SHA256', STRINGTOUTF8(:password),1000)")
	  @Mapper(UserResultSetMapper.class)
	  User authentify( @Bind("community") String community, @Bind("email") String email, @Bind("password") String password );

	  @SqlQuery("select * from user where community=:community and UPPER(email)=:email")
	  @Mapper(UserResultSetMapper.class)
	  User findExistingUser( @Bind("community") String community, @Bind("email") String email );

	  @SqlUpdate("delete from user where community = :community and UPPER(email) = :email")
	  void delete( @Bind("community") String community, @Bind("email") String email );

	  @SqlQuery("select * from user")
	  @Mapper(UserResultSetMapper.class)
	  List<User> findUsers();

}
