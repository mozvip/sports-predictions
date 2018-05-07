package predictions.model.db;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


@RegisterRowMapper(MatchPredictionResultSetMapper.class)
public interface MatchPredictionDAO {

	@SqlUpdate("merge into match_prediction (community, email, match_id, home_score, away_score, home_team_name, away_team_name, home_winner) values (:community, :email, :match_id, :home_score, :away_score, :home_team_name, :away_team_name, :home_winner)")
	void merge(@Bind("community") String community, @Bind("email") String email, @Bind("match_id") int match_id,
			   @Bind("home_score") int home_score, @Bind("away_score") int away_score,
			   @Bind("home_team_name") String home_team_name, @Bind("away_team_name") String away_team_name,
			   @Bind("home_winner") boolean home_winner);

	@SqlQuery("select match_prediction.* from match_prediction inner join user on match_prediction.community = user.community and match_prediction.email = user.email where user.active=true and match_prediction.community=:community and match_id = :gameId")
	List<MatchPrediction> findByGame(@Bind("community") String community, @Bind("gameId") int gameId);

	@SqlQuery("select * from match_prediction where community=:community AND UPPER(email)=UPPER(:email)")
	List<MatchPrediction> findForUser(@Bind("community") String community, @Bind("email") String email);

	@SqlQuery("select * from match_prediction")
	List<MatchPrediction> findAll();

	@SqlQuery("select * from match_prediction WHERE match_id=:gameNum")
	List<MatchPrediction> findPredictions(@Bind("gameNum") int gameNum);

	@SqlUpdate("update match_prediction set score=:score where community=:community and email=:email and match_id=:gameNum")
	void updateScore(@Bind("community") String community, @Bind("email") String email, @Bind("gameNum") int gameNum, @Bind("score") int score);

}
