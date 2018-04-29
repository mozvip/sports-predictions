package predictions.model.db;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface  ActualResultDAO {

	  @SqlUpdate("merge into actual_result ( match_id, home_score, away_score, home_team_name, away_team_name, home_winner, validated) values (:match_id, :home_score, :away_score, :home_team_name, :away_team_name, :home_winner, true)")
	  void merge(@Bind("match_id") int match_id, @Bind("home_score") int home_score, @Bind("away_score") int away_score, @Bind("home_team_name") String home_team_name, @Bind("away_team_name") String away_team_name, @Bind("home_winner") boolean home_winner);
	  
	  @SqlQuery("select * from actual_result where validated=true")
	  @Mapper(ActualResultResultSetMapper.class)
	  List<ActualResult> findValidated();
	  
	  @SqlQuery("select * from actual_result")
	  @Mapper(ActualResultResultSetMapper.class)
	  List<ActualResult> findAll();

	  @SqlQuery("select * from actual_result where match_id=:matchId")
	  @Mapper(ActualResultResultSetMapper.class)
	  ActualResult find( @Bind("matchId") int matchId );
	  
	  
}
