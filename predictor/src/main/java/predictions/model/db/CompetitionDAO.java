package predictions.model.db;

import com.github.mozvip.footballdata.model.Competition;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface CompetitionDAO {
	
	@SqlUpdate("MERGE INTO COMPETITIONS KEY(ID) VALUES(:competition.id, :competition.caption, :competition.league, :competition.year, :competition.currentMatchday, :competition.numberOfMatchdays, :competition.numberOfTeams, :competition.numberOfGames, :competition.lastUpdated)")
	void saveCompetition(@BindBean("competition") Competition competition);
	
	@SqlQuery("SELECT * FROM COMPETITIONS WHERE ID = :id")
	@Mapper(CompetitionMapper.class)
	Community getCompetition(@Bind("id") int id);

}
