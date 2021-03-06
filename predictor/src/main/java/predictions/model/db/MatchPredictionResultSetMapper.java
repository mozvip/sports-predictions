package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MatchPredictionResultSetMapper implements RowMapper<MatchPrediction> {

	public MatchPrediction map(ResultSet r, StatementContext ctx) throws SQLException {
		return new MatchPrediction(r.getString("community"), r.getString("email").toLowerCase(), r.getInt("match_id"),
				r.getInt("away_score"), r.getString("away_team_name"), r.getInt("home_score"),
				r.getString("home_team_name"), r.getBoolean("home_winner"), r.getInt("score"));
	}

}
