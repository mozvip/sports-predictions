package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserResultSetMapper implements RowMapper<User> {

	@Override
	public User map(ResultSet r, StatementContext ctx) throws SQLException {
		return new User(r.getString("community"), r.getString("name"), r.getString("email"), r.getString("password"), r.getString("CHANGE_PASSWORD_TOKEN"),
				new DateTime(r.getDate("LAST_LOGIN_DATE")),
				r.getInt("CURRENT_SCORE"), r.getInt("RANKING"), r.getInt("PREVIOUS_RANKING"),
				r.getBoolean("admin"), r.getBoolean("active") );
	}

}
