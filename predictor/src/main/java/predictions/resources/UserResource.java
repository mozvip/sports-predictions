package predictions.resources;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.model.ActualResult;
import predictions.model.ActualResultDAO;
import predictions.model.AuthenticationResult;
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

	@Context private HttpServletRequest httpRequest;

	public UserResource( UserDAO dao, MatchPredictionDAO matchPredictionDAO, ActualResultDAO actualResultDAO ) {
		this.userDAO = dao;
		this.matchPredictionDAO = matchPredictionDAO;
		this.actualResultDAO = actualResultDAO;
		this.matchPredictionDAO.createTable();
	}

	@RolesAllowed("ADMIN")
	@DELETE
	@ApiOperation(value="Deletes a user from the application")
	public void deleteUser( @Auth User user, @QueryParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		this.userDAO.delete( community, email.toLowerCase() );
	}

	@POST
	@Path("/savePredictions")
	@Timed
	public AuthenticationResult createUser( MatchPredictions predictions ) {

		AuthenticationResult result = new AuthenticationResult();
		
		String community = (String) httpRequest.getAttribute("community");

		User user = userDAO.findExistingUser(community, predictions.getEmail() );
		if (user == null) {
			userDAO.insert( predictions.getEmail(), predictions.getName(), community, predictions.getPassword() );
			savePredictions( community, predictions.getEmail(), predictions );
			String authToken = generateAuthToken(community, predictions.getEmail(), predictions.getPassword());
			result.setAuthToken( authToken );
		} else {
			result.setMessage("Cet email est déjà utilisé par un utilisateur");
		}

		return result;
	}

	@POST
	@Path("/create")
	@ApiOperation("Create a new regular user")
	public void createUser(@FormParam("email") String email, @FormParam("name") String name, @FormParam("password") String password) {
		String community = (String) httpRequest.getAttribute("community");
		userDAO.insert(email, name, community, password );
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
		return buildPredictions( user.getCommunity(), user.getEmail() );
	}
	
	private MatchPredictions buildPredictions( String community, String email ) {
		MatchPredictions predictions = new MatchPredictions();
		List<MatchPrediction> matchPredictions = matchPredictionDAO.findForUser(community, email);
		predictions.setMatch_predictions_attributes( matchPredictions );
		return predictions;
		
	}
	
	private String generateAuthToken( String community, String email, String password ) {
		return new String( Base64.getEncoder().encode( String.format("%s$$_$$%s:%s", community, email, password).getBytes() ) );		
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
	public void forgetPassword( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		UUID uuid = UUID.randomUUID();
		userDAO.setChangePasswordToken(community, email, uuid);
		
		// TODO : send email
		
	}

	@POST
	@Path("/signin")
	@ApiOperation("Used to login a user")
	public MatchPredictions signIn( @FormParam("email") String email, @FormParam("password") String password ) {

		String community = (String) httpRequest.getAttribute("community");
		
		MatchPredictions predictions = null;
		if( userDAO.authentify(community, email.toLowerCase().trim(), password) != null ) {
			predictions = buildPredictions( community, email );
			predictions.setAuthToken( generateAuthToken(community, email, password) );
		}

		return predictions;
	}
}
