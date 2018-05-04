package predictions.model.db;

import com.github.mozvip.footballdata.model.Competition;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterRowMapper(CompetitionMapper.class)
public interface CompetitionDAO {
	
	@SqlUpdate("MERGE INTO COMPETITIONS KEY(ID) VALUES(:competition.id, :competition.caption, :competition.league, :competition.year, :competition.currentMatchday, :competition.numberOfMatchdays, :competition.numberOfTeams, :competition.numberOfGames, :competition.lastUpdated)")
	void saveCompetition(@BindBean("competition") Competition competition);
	
	@SqlQuery("SELECT * FROM COMPETITIONS WHERE ID = :id")
	Community getCompetition(@Bind("id") int id);

}
