package predictions.model;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface MatchPredictionDAO {

	  @SqlUpdate("merge into match_prediction (community, email, match_id, home_score, away_score, home_team_id, away_team_id, home_winner) values (:community, :email, :match_id, :home_score, :away_score, :home_team_id, :away_team_id, :home_winner)")
	  void insert(@Bind("community") String community, @Bind("email") String email, @Bind("match_id") int match_id, @Bind("home_score") int home_score, @Bind("away_score") int away_score, @Bind("home_team_id") String home_team_id, @Bind("away_team_id") String away_team_id, @Bind("home_winner") boolean home_winner);
	  
	  @SqlQuery("select * from match_prediction where community=:community")
	  @Mapper(MatchPredictionResultSetMapper.class)
	  List<MatchPrediction> findByCommunity(@Bind("community") String community);

	  @SqlQuery("select * from match_prediction where community=:community AND UPPER(email)=UPPER(:email)")
	  @Mapper(MatchPredictionResultSetMapper.class)
	  List<MatchPrediction> findForUser(@Bind("community") String community, @Bind("email") String email);

	  @SqlQuery("select * from match_prediction")
	  @Mapper(MatchPredictionResultSetMapper.class)
	  List<MatchPrediction> findAll();

}
