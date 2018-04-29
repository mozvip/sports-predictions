package predictions.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class MatchPredictionResultSetMapper implements ResultSetMapper<MatchPrediction> {

	public MatchPrediction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new MatchPrediction(r.getString("community"), r.getString("email").toLowerCase(), r.getInt("match_id"),
				r.getInt("away_score"), r.getString("away_team_id"), r.getInt("home_score"),
				r.getString("home_team_id"), r.getBoolean("home_winner"), r.getInt("score"));
	}

}
