package predictions.resources;

import com.github.mozvip.footballdata.model.Teams;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.PredictionsConfiguration;
import predictions.model.db.Team;
import predictions.model.db.TeamDAO;
import predictions.model.db.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
@Api
public class TeamsResource {

    private final static Logger logger = LoggerFactory.getLogger( Teams.class );

    TeamDAO teamDAO;
    PredictionsConfiguration configuration;

    @Context
    private HttpServletRequest httpRequest;

    public TeamsResource(PredictionsConfiguration configuration, TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
        this.configuration = configuration;
    }

    @GET
    public List<Team> getTeams(@ApiParam(hidden = true) @Auth User user) {
        return teamDAO.getTeams();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void createTeam(@ApiParam(hidden = true) @Auth User user,
                           @NotNull @FormDataParam("name") String name,
                           @NotNull @FormDataParam("description") String description,
                           @FormDataParam("file") InputStream uploadedInputStream,
                           @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        logger.info(String.format("Creating new team : name=%s description=%s", name, description));

        String community = (String) httpRequest.getAttribute("community");

        teamDAO.createTeam(community, name, description);
        if (uploadedInputStream != null) {
            String fileName = String.format("%s.jpg", name);
            java.nio.file.Path imageFile = configuration.getDataFolder().resolve("teams").resolve(fileName);
            Files.createDirectories(imageFile.getParent());
            Files.copy(uploadedInputStream, imageFile);
        }
    }

    @DELETE
    @Path("/{name}")
    public void deleteTeam(@ApiParam(hidden = true) @Auth User user, @PathParam("name") String name) {

        String community = (String) httpRequest.getAttribute("community");
        teamDAO.deleteTeam(community, name);
    }
}
