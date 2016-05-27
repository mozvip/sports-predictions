package predictions.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.PredictionsConfiguration;
import predictions.gmail.GmailService;
import predictions.model.ActualResult;
import predictions.model.ActualResultDAO;
import predictions.model.MatchPrediction;
import predictions.model.MatchPredictionDAO;
import predictions.model.MatchPredictions;
import predictions.model.Rankings;
import predictions.model.User;
import predictions.model.UserDAO;
import predictions.views.CreateUserView;
import predictions.views.ForgetPasswordPageView;
import predictions.views.SigninPageView;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	private final static Logger logger = LoggerFactory.getLogger( UserResource.class );

	private UserDAO userDAO;
	private MatchPredictionDAO matchPredictionDAO;
	private ActualResultDAO actualResultDAO;
	private HttpClient client;
	private PredictionsConfiguration configuration;
	private GmailService gmail;

	@Context private HttpServletRequest httpRequest;

	public UserResource( UserDAO dao, MatchPredictionDAO matchPredictionDAO, ActualResultDAO actualResultDAO, HttpClient client, PredictionsConfiguration configuration, GmailService gmail ) {
		this.userDAO = dao;
		this.matchPredictionDAO = matchPredictionDAO;
		this.actualResultDAO = actualResultDAO;
		this.client = client;
		this.configuration = configuration;
		this.gmail = gmail;
	}

	@RolesAllowed("ADMIN")
	@DELETE
	@ApiOperation(value="Deletes a user from the application")
	public void deleteUser( @Auth User user, @QueryParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		userDAO.delete( community, email );
	}

	@POST
	@Path("/savePredictions")
	@Timed
	public void savePredictions( @Auth User user, MatchPredictions predictions ) {
		String community = (String) httpRequest.getAttribute("community");
		savePredictions( community, predictions.getEmail(), predictions );
	}

	@POST
	@Path("/create")
	@ApiOperation("Create a new regular user")
	public void createUser(@FormParam("email") String email, @FormParam("name") String name, @FormParam("password") String password, @FormParam("g-recaptcha-response") String recaptcha) throws IOException {
		
		recaptcha(recaptcha);
		
		String community = (String) httpRequest.getAttribute("community");
		
		password = password.trim();
		email = email.trim().toLowerCase();
		
		User user = userDAO.findExistingUser(community, email );
		if (user == null) {
			userDAO.insert(email, name, community, password );
		} else {
			logger.warn("Attempt to create an already existing user : {} on community {}", email, community);
		}
	}

	private void recaptcha(String recaptcha) throws UnsupportedEncodingException, IOException, ClientProtocolException,
			JsonParseException, JsonMappingException {
		
		if (!configuration.isGoogleReCaptchaEnabled()) {
			return;
		}
		
		HttpPost post = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
		List<NameValuePair> postParams = new ArrayList<>();
		postParams.add( new BasicNameValuePair("secret", configuration.getGoogleReCaptchaSecretKey()) );
		postParams.add( new BasicNameValuePair("response", recaptcha) );
		postParams.add( new BasicNameValuePair("remoteip", httpRequest.getRemoteAddr()) );
		post.setEntity( new UrlEncodedFormEntity(postParams));

		String string = null;
		HttpResponse clientResponse = client.execute( post );
		string = EntityUtils.toString( clientResponse.getEntity() );
		
		ObjectMapper mapper = new ObjectMapper();
		GoogleReCaptchaResponse response = mapper.readValue( string, GoogleReCaptchaResponse.class );
		if (!response.isSuccess()) {
			throw new IOException("reCAPTCHA check failed");
		}
	}
	
	@RolesAllowed("ADMIN")
	@POST
	@Path("/set-admin")
	@ApiOperation(value="Gives or remove admin privileges to an existing user, can only be invoked by a connected admin")
	public void setAdmin(@Auth User user, @FormParam("email") String email, @FormParam("admin") boolean admin) {
		String community = (String) httpRequest.getAttribute("community");
		userDAO.setAdmin( community, email, admin );
	}
	
	@GET
	@Path("/availability")
	@ApiOperation("Indicates if the email is available for new users")
	public boolean isAvailable(@QueryParam("email") String email) {
		String community = (String) httpRequest.getAttribute("community");
		return userDAO.findExistingUser(community, email) == null;
	}
	
	@GET
	@Path("/create")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="Display the view to create a new user", hidden=true)
	public CreateUserView createUser() {
		return new CreateUserView( (String) httpRequest.getAttribute("community") );	
	}	
	
	private void savePredictions( String community, String email, MatchPredictions predictions ) {
		Set<Integer> validatedMatches = new HashSet<Integer>();
		List<ActualResult> result = actualResultDAO.findValidated();
		for (ActualResult actualResult : result) {
			validatedMatches.add( actualResult.getMatch_id() );
		}
		for (MatchPrediction prediction : predictions.getMatch_predictions_attributes()) {
			if (!validatedMatches.contains( prediction.getMatch_id())) {
				matchPredictionDAO.insert(community, email, prediction.getMatch_id(), prediction.getHome_score(), prediction.getAway_score(), prediction.getHome_team_id(), prediction.getAway_team_id(), prediction.isHome_winner());
			}
		}
	}
	
	@POST
	@Path("/save")
	@Timed
	@ApiOperation("Save predictions for the connected user")
	public void save( @Auth User user, MatchPredictions predictions ) {
		savePredictions( user.getCommunity(), user.getEmail(), predictions );
	}

	@GET
	@Path("/rankings")
	@ApiOperation("Return the current rankings")
	public Rankings getRankings() {
		String community = (String) httpRequest.getAttribute("community");
		return new Rankings( userDAO.findUsersOrderedByScore( community ) );
	}	
	
	@GET
	@Path("/predictions")
	@ApiOperation("Get the current predictions for the connected user")
	public MatchPredictions getPredictions( @Auth User user ) {
		return buildPredictions( user );
	}
	
	private MatchPredictions buildPredictions( User user ) {
		MatchPredictions predictions = new MatchPredictions();
		List<MatchPrediction> matchPredictions = matchPredictionDAO.findForUser( user.getCommunity(), user.getEmail() );
		predictions.setCommunity( user.getCommunity() );
		predictions.setEmail( user.getEmail() );
		predictions.setName( user.getName() );
		predictions.setAdmin( user.isAdmin() || user.getCommunity().equals("localhost") );
		predictions.setMatch_predictions_attributes( matchPredictions );
		return predictions;
		
	}
	
	private String generateAuthToken( String community, String email, String password ) {
		return new String( Base64.getEncoder().encode( String.format("%s:%s", email, password).getBytes() ) );		
	}

	@GET
	@Path("/signin")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="Display the view to signin", hidden=true)
	public SigninPageView signIn() {
		return new SigninPageView();
	}

	@GET
	@Path("/forget-password")
	@Produces(MediaType.TEXT_HTML)
	@ApiOperation(value="Display the view to declare a lost password", hidden=true)
	public ForgetPasswordPageView forgetPasswordGetView() {
		return new ForgetPasswordPageView();
	}

	@POST
	@Path("/forget-password")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Declares a forgotten password and send the relevant email")
	public Response forgetPassword( @FormParam("email") String email, @FormParam("g-recaptcha-response") String recaptcha ) throws IOException, MessagingException {
		recaptcha(recaptcha);

		String community = (String) httpRequest.getAttribute("community");
		email = email.trim();
		
		User existingUser = userDAO.findExistingUser(community, email);
		if (existingUser == null) {
			return Response.status(404).build();
		}
		
		UUID uuid = UUID.randomUUID();
		userDAO.setChangePasswordToken(community, email, uuid);
		
		String mailFrom = getAdminEmail(community);
		
		String subject = String.format( "Mot de passe oublié pour https://%s.pronostics2016.com", community);
		
		String resetPasswordLink = String.format("https://%s.pronostics2016.com/#/forget-password/%s/%s", community, email, uuid.toString());			
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
	@ApiOperation("Used to login a user")
	public MatchPredictions signIn( @FormParam("email") String email, @FormParam("password") String password ) {

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
