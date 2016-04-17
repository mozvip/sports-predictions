package predictions.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class UserResultSetMapper implements ResultSetMapper<User> {

	public User map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {
		
		return new User(r.getString("community"), r.getString("name"), r.getString("email"), r.getString("password"), r.getString("CHANGE_PASSWORD_TOKEN"),
				r.getDate("LAST_LOGIN_DATE"),
				r.getInt("CURRENT_SCORE"),
				r.getBoolean("admin") );
		
	}





}
