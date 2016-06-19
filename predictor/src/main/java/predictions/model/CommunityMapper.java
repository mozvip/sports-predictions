
package predictions.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CommunityMapper implements ResultSetMapper<Community> {

	@Override
	public Community map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		return new Community(r.getString("COMMUNITY_NAME"), r.getBoolean("CREATE_ACCOUNT_ENABLED"),
				r.getBoolean("GROUPS_EDIT_ENABLED"), r.getBoolean("FINALS_EDIT_ENABLED"));
	}

}
