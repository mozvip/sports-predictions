package predictions.resources;

import com.github.mozvip.footballdata.FootballDataClient;
import com.github.mozvip.footballdata.model.*;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import predictions.model.db.ActualResultDAO;
import predictions.model.db.CompetitionDAO;
import predictions.model.db.User;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/competition")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompetitionResource {

    private FootballDataClient client;
    private CompetitionDAO competitionDAO;
    private ActualResultDAO actualResultDAO;

    public CompetitionResource(FootballDataClient client, CompetitionDAO competitionDAO, ActualResultDAO actualResultDAO) {
        this.client = client;
        this.competitionDAO = competitionDAO;
        this.actualResultDAO = actualResultDAO;
    }

    @POST
    @RolesAllowed("ADMIN")
    @ApiOperation(tags={"admin", "competition"}, value = "Save or create a competition", authorizations = @Authorization("basicAuth"))
    public Competition saveOrCreateCompetition(@Auth @ApiParam(hidden = true) User user, @NotNull  Integer competitionId) throws IOException {
        Competition competition = client.competition(competitionId);
        competitionDAO.saveCompetition(competition);

        Teams teams = client.teams(competitionId);
        for (Team team : teams.getTeams()) {

        }

        Fixtures fixtures = client.fixtures(competitionId);
        for (Fixture fixture : fixtures.getFixtures()) {
            if (fixture.getStatus() == FixtureStatus.SCHEDULED) {

            }
        }
        return competition;
    }

    @PUT
    @RolesAllowed("ADMIN")
    @ApiOperation(tags={"admin", "competition"}, value = "Update competition, obtaining scores for finished games", authorizations = @Authorization("basicAuth"))
    public void updateCompetition(@Auth @ApiParam(hidden = true) User user, @NotNull Integer competitionId) throws IOException {
        Competition competition = client.competition(competitionId);
        competitionDAO.saveCompetition(competition);

        Fixtures fixtures = client.fixtures(competitionId);
        for (Fixture fixture : fixtures.getFixtures()) {
            if (fixture.getStatus() == FixtureStatus.SCHEDULED) {

            }
        }
    }


}
