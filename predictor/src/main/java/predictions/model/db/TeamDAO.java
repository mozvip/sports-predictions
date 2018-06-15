package predictions.model.db;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(TeamRowMapper.class)
public interface TeamDAO {

    @SqlQuery("select * from team where community=:community")
    List<Team> getTeams(@Bind("community") String community);

    @SqlQuery("select team.* from team inner join user_teams on team.name = user_teams.team where user_teams.email=:email AND user_teams.community=:community")
    List<Team> getTeamsForUser(@Bind("community") String community, @Bind("email") String email);

    @SqlUpdate("insert into user_teams values (:community, :email, :team)")
    void associateUserToTeam(@Bind("community") String community, @Bind("email") String email, @Bind("team") String team);

    @SqlUpdate("delete from user_teams where email=:email and community=:community and team=:team")
    void removeUserFromTeam(@Bind("community") String community, @Bind("email") String email, @Bind("team") String team);

    @SqlUpdate("delete from team where community=:community and name=:name")
    void deleteTeam(@Bind("community") String community, @Bind("name") String name);

    @SqlUpdate("insert into team values(:community, :name, :description, :ownerEmail)")
    void createTeam(@Bind("community") String community, @Bind("name") String name, @Bind("description") String description, @Bind("ownerEmail") String ownerEmail);

    @SqlQuery("select * from team where community=:community and name=:name")
    Team getTeam(@Bind("community") String community, @Bind("name") String name);
}
