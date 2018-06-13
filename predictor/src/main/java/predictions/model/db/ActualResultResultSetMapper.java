package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActualResultResultSetMapper implements RowMapper<ActualResult> {

	public ActualResult map(ResultSet r, StatementContext ctx)
			throws SQLException {
		return new ActualResult( r.getInt("match_id"), r.getInt("away_score"), r.getString("AWAY_TEAM_NAME"), r.getInt("home_score"), r.getString("HOME_TEAM_NAME"), r.getBoolean("home_winner"), r.getBoolean("validated"));
	}

}
