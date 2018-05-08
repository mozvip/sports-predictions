package predictions.resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.PredictionsConfiguration;
import predictions.gmail.GmailService;
import predictions.model.GoogleReCaptchaResponse;
import predictions.model.MatchPredictions;
import predictions.model.Rankings;
import predictions.model.db.*;
import predictions.phases.Phase;
import predictions.phases.PhaseManager;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Api
@SwaggerDefinition(
	securityDefinition = @SecurityDefinition(
		basicAuthDefinitions = @BasicAuthDefinition(key="basicAuth"),
		oAuth2Definitions = @OAuth2Definition(key="oauth2", scopes = {@Scope(name = "email", description = "email")}, tokenUrl = "https://www.googleapis.com/oauth2/v4/token", authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth", flow=OAuth2Definition.Flow.ACCESS_CODE)
	)
)
public class UserResource {

	private final static Logger LOGGER = LoggerFactory.getLogger( UserResource.class );

    private PhaseManager phaseManager;

	private UserDAO userDAO;
	private MatchPredictionDAO matchPredictionDAO;
	private ActualResultDAO actualResultDAO;
	private CommunityDAO communityDAO;
	
	private HttpClient client;
	private PredictionsConfiguration configuration;
	private GmailService gmail;

	@Context private HttpServletRequest httpRequest;

	public UserResource( PhaseManager phaseManager, UserDAO dao, MatchPredictionDAO matchPredictionDAO, ActualResultDAO actualResultDAO, CommunityDAO communityDAO, HttpClient client, PredictionsConfiguration configuration, GmailService gmail ) {
		this.phaseManager = phaseManager;
		this.userDAO = dao;
		this.matchPredictionDAO = matchPredictionDAO;
		this.actualResultDAO = actualResultDAO;
		this.communityDAO = communityDAO;
		this.client = client;
		this.configuration = configuration;
		this.gmail = gmail;
	}
	
	@GET
	@Path("/emailAvailable")
	@ApiOperation(tags="public", value="Indicates if the email is available for new users")
	public boolean isEmailAvailable(@NotNull @QueryParam("email") String email) {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findUser(community, email) == null;
	}
	
	@GET
	@Path("/nameAvailable")
	@ApiOperation(tags="public", value="Indicates if the name is available for new users")
	public boolean isNameAvailable(@QueryParam("email") String email, @QueryParam("name") String name) {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findExistingUserByName(community, email, name) == null;
	}

	@GET
	@Path("/count")
	@ApiOperation(tags="public", value="Returns the current user count for the community")
	public long getUserCount() {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.getCount(community);
	}


	@DELETE
	@Path("/{email}")
	@RolesAllowed("ADMIN")
	@ApiOperation(tags="admin", value="Deletes a user from the application", authorizations = @Authorization("basicAuth"))
	public void deleteUser(@NotNull @PathParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		userDAO.delete( community, email );
	}

	@POST
	@Path("/create")
	@ApiOperation(tags={"public", "captcha"}, value="Create a new regular user")
	public void createUser(@NotNull @FormParam("email") String email, @NotNull @FormParam("name") String name, @NotNull @FormParam("password") String password, @NotNull @FormParam("g-recaptcha-response") String recaptcha) throws IOException {
		
		String communityName = (String) httpRequest.getAttribute("community");
		Community community = communityDAO.getCommunity(communityName);

		if (community == null) {
			// FIXME: create new community with default settings ??
			community = new Community(communityName, true, null, AccessType.W, AccessType.W);
			communityDAO.updateCommunity(community.getName(), community.isCreateAccountEnabled(), community.getOpeningDate(), community.getGroupsAccess(), community.getFinalsAccess());
		} else if (!community.isCreateAccountEnabled()) {
			return;	// FIXME: error code 403
		}
		
		recaptcha(recaptcha);
		
		password = password.trim();
		email = email.trim().toLowerCase();
		
		User user = userDAO.findUser(communityName, email );
		if (user == null) {
			userDAO.insert(email, name, communityName, password, configuration.getAdministratorAccounts() != null && configuration.getAdministratorAccounts().contains(email) );
		} else {
			LOGGER.warn("Attempt to create an already existing user : {} on community {}", email, communityName);
		}
	}

	private void recaptcha(String recaptcha) throws IOException {
		
		if (!configuration.isGoogleReCaptchaEnabled()) {
			return;
		}
		
		HttpPost post = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
		List<NameValuePair> postParams = new ArrayList<>();
		postParams.add( new BasicNameValuePair("secret", configuration.getGoogleReCaptchaSecretKey()) );
		postParams.add( new BasicNameValuePair("response", recaptcha) );
		postParams.add( new BasicNameValuePair("remoteip", httpRequest.getRemoteAddr()) );
		post.setEntity( new UrlEncodedFormEntity(postParams));

		HttpResponse clientResponse = client.execute( post );
		String string = EntityUtils.toString( clientResponse.getEntity() );
		
		ObjectMapper mapper = new ObjectMapper();
		GoogleReCaptchaResponse response = mapper.readValue( string, GoogleReCaptchaResponse.class );
		if (!response.isSuccess()) {
			throw new IOException("reCAPTCHA check failed");
		}
	}
	
	@RolesAllowed("ADMIN")
	@POST
	@Path("/set-admin")
	@ApiOperation(tags="admin", value="Gives or remove admin privileges to an existing user, can only be invoked by an admin", authorizations = @Authorization("basicAuth"))
	public void setAdmin(@ApiParam(hidden = true) @Auth User user, @NotNull @FormParam("email") String email, @NotNull @FormParam("admin") boolean admin) {
		String community = (String) httpRequest.getAttribute("community");
		userDAO.setAdmin( community, email, admin );
	}

	private void savePredictions( Community community, String email, MatchPredictions predictions, boolean forceSave ) {
		Set<Integer> validatedMatches = new HashSet<Integer>();
		List<ActualResult> result = actualResultDAO.findValidated();
		for (ActualResult actualResult : result) {
			validatedMatches.add( actualResult.getMatch_id() );
		}
		for (MatchPrediction prediction : predictions.getMatch_predictions_attributes()) {
			if (forceSave || !validatedMatches.contains( prediction.getMatch_id())) {
				if (prediction.getHome_team_name() != null && prediction.getAway_team_name() != null) {
					matchPredictionDAO.merge(community.getName(), email, prediction.getMatch_id(), prediction.getHome_score(), prediction.getAway_score(), prediction.getHome_team_name(), prediction.getAway_team_name(), prediction.isHome_winner());
				}
			}
		}
	}
	
	@POST
	@Path("/saveProfile")
	@ApiOperation(tags="user", value="Save user profile", authorizations = @Authorization("basicAuth"))
	public void saveProfile(@ApiParam(hidden = true) @Auth User user, @FormParam("name") String name ) {
		name = name.trim();
		userDAO.setName( user.getCommunity(), user.getEmail(), name );
	}
	
	@POST
	@Path("/save")
	@Timed
	@ApiOperation(tags="user", value="Save predictions for the connected user", authorizations = @Authorization("basicAuth"))
	public void save(@ApiParam(hidden = true) @Auth User user, MatchPredictions predictions ) {

		String name = (String) httpRequest.getAttribute("community");
		Community community = communityDAO.getCommunity(name);

		// FIXME: only save relevant data !! use currentPhase !!
		Phase currentPhase = phaseManager.getCurrentPhase();
		if (community.getFinalsAccess() == AccessType.W || community.getGroupsAccess() == AccessType.W) {
			savePredictions( community, user.getEmail(), predictions, false );
		}
	}
	
	@POST
	@Path("/save-impersonate")
	@Timed
	@ApiOperation(tags="admin", value="Save predictions for *another* user", authorizations = @Authorization("basicAuth"))
	@RolesAllowed("ADMIN")
	public void saveImpersonate(@ApiParam(hidden = true) @Auth User user, MatchPredictions predictions ) {
		String name = (String) httpRequest.getAttribute("community");
		Community community = communityDAO.getCommunity(name);
		savePredictions( community, predictions.getEmail(), predictions, true );
	}	

	@GET
	@Path("/rankings")
	@ApiOperation(tags="public", value="Return the current rankings")
	public Rankings getRankings() {
		String community = (String) httpRequest.getAttribute("community");
		return new Rankings( userDAO.findUsersOrderedByScore( community ) );
	}	
	
	@GET
	@Path("/predictions")
	@ApiOperation(tags="user", value="Get the current predictions for the connected user", authorizations = @Authorization("basicAuth"))
	public MatchPredictions getPredictions(@ApiParam(hidden = true) @Auth User user ) {
		return buildPredictions( user );
	}
	
	@GET
	@Path("/predictions/{email}")
	@RolesAllowed("ADMIN")
	@ApiOperation(tags="admin", value="Get the current predictions for the specified user", authorizations = @Authorization("basicAuth"))
	public MatchPredictions getPredictionsForUser(@ApiParam(hidden = true) @Auth User user, @NotNull @PathParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		email = email.trim().toLowerCase();
		return buildPredictions( userDAO.findUser(community, email) );
	}
	
	
	private MatchPredictions buildPredictions( User user ) {
		MatchPredictions predictions = new MatchPredictions();
		predictions.setCommunity( user.getCommunity() );
		predictions.setEmail( user.getEmail() );
		predictions.setName( user.getName() );
		predictions.setCurrentRanking( user.getCurrentRanking() );
		predictions.setAdmin( user.isAdmin() || user.getCommunity().equals("localhost") );

		List<MatchPrediction> matchPredictions = matchPredictionDAO.findForUser( user.getCommunity(), user.getEmail() );
		predictions.setMatch_predictions_attributes( matchPredictions );
		
		return predictions;
		
	}
	
	private String generateAuthToken( String community, String email, String password ) {
		return new String( Base64.getEncoder().encode( String.format("%s:%s", email, password).getBytes() ) );		
	}

	@POST
	@Path("/forget-password")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(tags={"public", "captcha"}, value="Declares a forgotten password and send the relevant email")
	public Response forgetPassword(@NotNull @FormParam("email") String email,@NotNull @FormParam("g-recaptcha-response") String recaptcha ) throws IOException, MessagingException {
		recaptcha(recaptcha);

		String community = (String) httpRequest.getAttribute("community");
		email = email.trim();
		
		User existingUser = userDAO.findUser(community, email);
		if (existingUser == null) {
			return Response.status(404).build();
		}
		
		UUID uuid = UUID.randomUUID();
		userDAO.setChangePasswordToken(community, email, uuid);
		
		String mailFrom = getAdminEmail(community);
		
		String subject = String.format("Mot de passe oublié pour https://%s.%s", community, configuration.getPublicDomain());
		
		String resetPasswordLink = String.format("https://%s.%s/#/forget-password/%s/%s", community, configuration.getPublicDomain(), email, uuid.toString());
		String htmlMessage = String.format( "<p>Cliquez <a href='%s'>ce lien</a> pour choisir un nouveau mot de passe</p>", resetPasswordLink );

		gmail.sendEmail( existingUser.getEmail(), subject, htmlMessage );

		
		return Response.ok().build();
	}

	public String getAdminEmail(String community) {
		List<User> communityAdmins = userDAO.findAdmins( community );
		String mailFrom = "guillaume.serre@gmail.com";
		if (communityAdmins != null && communityAdmins.size() > 0) {
			mailFrom = communityAdmins.get(0).getEmail();
		}
		return mailFrom;
	}

	@POST
	@Path("/signin")
	@ApiOperation(tags="public", value="Used to login a user")
	public MatchPredictions signIn(@NotNull @FormParam("email") String email, @NotNull @FormParam("password") String password ) {

		String community = (String) httpRequest.getAttribute("community");
		email = email.toLowerCase().trim();
		password = password.trim();
		
		MatchPredictions predictions = null;
		User user = userDAO.authentify(community, email, password);
		if( user != null ) {
			userDAO.updateLastLoginDate(community, email);
			predictions = buildPredictions( user );
			predictions.setAuthToken( generateAuthToken(community, email, password) );
		}

		return predictions;
	}
}
