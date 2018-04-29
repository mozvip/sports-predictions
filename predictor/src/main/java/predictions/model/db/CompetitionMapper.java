package predictions.model.db;

import com.github.mozvip.footballdata.model.Competition;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class CompetitionMapper implements ResultSetMapper<Competition> {

    @Override
    public Competition map(int index, ResultSet r, StatementContext ctx) throws SQLException {

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
