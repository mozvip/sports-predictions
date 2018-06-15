package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamRowMapper implements RowMapper<Team> {
    @Override
    public Team map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Team(
                rs.getString("COMMUNITY"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getString("OWNER")
        );
    }
}
