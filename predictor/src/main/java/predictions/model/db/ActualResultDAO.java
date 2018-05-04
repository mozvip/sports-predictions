package predictions.model.db;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(ActualResultResultSetMapper.class)
public interface  ActualResultDAO {

	  @SqlUpdate("merge into actual_result ( match_id, home_score, away_score, home_team_name, away_team_name, home_winner, validated) values (:match_id, :home_score, :away_score, :home_team_name, :away_team_name, :home_winner, true)")
	  void merge(@Bind("match_id") int match_id, @Bind("home_score") int home_score, @Bind("away_score") int away_score, @Bind("home_team_name") String home_team_name, @Bind("away_team_name") String away_team_name, @Bind("home_winner") boolean home_winner);
	  
	  @SqlQuery("select * from actual_result where validated=true")
	  List<ActualResult> findValidated();
	  
	  @SqlQuery("select * from actual_result")
	  List<ActualResult> findAll();

	  @SqlQuery("select * from actual_result where match_id=:matchId")
	  ActualResult find( @Bind("matchId") int matchId );

}
