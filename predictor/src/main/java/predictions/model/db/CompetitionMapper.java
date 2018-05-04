package predictions.model.db;

import com.github.mozvip.footballdata.model.Competition;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CompetitionMapper implements RowMapper<Competition> {

    @Override
    public Competition map(ResultSet r, StatementContext ctx) throws SQLException {

        Timestamp lastUpdated = r.getTimestamp("lastUpdated");
        ZonedDateTime zonedDateTime = lastUpdated != null ? ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastUpdated.getTime()), ZoneOffset.UTC) : null;

        return new Competition(
                r.getInt("id"),
                r.getString("caption"),
                r.getString("league"),
                r.getString("year"),
                r.getInt("currentMatchday"),
                r.getInt("numberOfMatchdays"),
                r.getInt("numberOfTeams"),
                r.getInt("numberOfGames"),
                zonedDateTime
        );
    }
}
