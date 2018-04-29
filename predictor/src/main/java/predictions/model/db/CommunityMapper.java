
package predictions.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CommunityMapper implements ResultSetMapper<Community> {

	@Override
	public Community map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Community(r.getString("COMMUNITY_NAME"), r.getBoolean("CREATE_ACCOUNT_ENABLED"),
				AccessType.valueOf(r.getString("GROUPS_ACCESS")), AccessType.valueOf(r.getString("FINALS_ACCESS")));
	}

}
