package predictions.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class ActualResultResultSetMapper implements ResultSetMapper<ActualResult> {

	public ActualResult map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		return new ActualResult( r.getInt("match_id"), r.getInt("away_score"), r.getString("away_team_id"), r.getInt("home_score"), r.getString("home_team_id"), r.getBoolean("home_winner"), r.getBoolean("validated"),
				r.getString("home_team_name"), r.getString("away_team_name") );
	}

}
