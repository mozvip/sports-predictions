
package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommunityMapper implements RowMapper<Community> {

	@Override
	public Community map(ResultSet r, StatementContext ctx) throws SQLException {
		return new Community(r.getString("COMMUNITY_NAME"), r.getBoolean("CREATE_ACCOUNT_ENABLED"),
				AccessType.valueOf(r.getString("GROUPS_ACCESS")), AccessType.valueOf(r.getString("FINALS_ACCESS")));
	}

}
