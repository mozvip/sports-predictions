package predictions.resources;

import com.github.mozvip.footballdata.FootballDataClient;
import com.github.mozvip.footballdata.model.*;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import predictions.model.db.ActualResultDAO;
import predictions.model.db.CompetitionDAO;
import predictions.model.db.User;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/competition")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api
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
    @ApiOperation(value = "Save or create a competition")
    public Competition saveOrCreateCompetition(@Auth @ApiParam(hidden = true) User user, Integer competitionId) throws IOException {
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
    public void updateCompetition(@Auth @ApiParam(hidden = true) User user, Integer competitionId) throws IOException {
        Competition competition = client.competition(competitionId);
        competitionDAO.saveCompetition(competition);

        Fixtures fixtures = client.fixtures(competitionId);
        for (Fixture fixture : fixtures.getFixtures()) {
            if (fixture.getStatus() == FixtureStatus.SCHEDULED) {

            }
        }
    }


}
