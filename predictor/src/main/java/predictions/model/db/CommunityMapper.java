
package predictions.model.db;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;

public class CommunityMapper implements RowMapper<Community> {

    @Override
    public Community map(ResultSet r, StatementContext ctx) throws SQLException {
        Timestamp openingDate = r.getTimestamp("OPENING_DATE");
        return new Community(r.getString("COMMUNITY_NAME"),
                r.getBoolean("CREATE_ACCOUNT_ENABLED"),
                openingDate != null ? openingDate.toLocalDateTime().atZone(ZoneId.of("UTC+2")) : null,
                AccessType.valueOf(r.getString("GROUPS_ACCESS")),
                AccessType.valueOf(r.getString("FINALS_ACCESS")));
    }

}
