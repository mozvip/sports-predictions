package predictions.model.db;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(TeamRowMapper.class)
public interface TeamDAO {

    @SqlQuery("select * from team")
    List<Team> getTeams();

    @SqlQuery("select team.* from team inner join user_teams on team.name = user_teams.team where user_teams.email=:email")
    List<Team> getTeamsForUser(@Bind("email") String email);

    @SqlUpdate("insert into user_teams values (:email, :team)")
    void associateUserToTeam(@Bind("email") String email, @Bind("team") String team);

    @SqlUpdate("delete from user_teams where email=:email and team=:team")
    void removeUserFromTeam(@Bind("email") String email, @Bind("team") String team);

    @SqlUpdate("delete from team where name=:name")
    void deleteTeam(@Bind("name") String name);

    @SqlUpdate("insert into team values(:name, :description)")
    void createTeam(@Bind("name") String name, @Bind("description") String description);

}
