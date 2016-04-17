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

	private UserDAO dao;
	private MatchPredictionDAO matchPredictionDAO;
	private ActualResultDAO actualResultDAO;

	@Context private HttpServletRequest httpRequest;

	public UserResource( UserDAO dao, MatchPredictionDAO matchPredictionDAO, ActualResultDAO actualResultDAO ) {
		this.dao = dao;
		this.matchPredictionDAO = matchPredictionDAO;
		this.actualResultDAO = actualResultDAO;
		this.matchPredictionDAO.createTable();
	}

	@RolesAllowed("ADMIN")
	@DELETE
	public void deleteUser( @Auth User user, @QueryParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		this.dao.delete( community, email.toLowerCase() );
	}

	@POST
	@Path("/savePredictions")
	@Timed
	public AuthenticationResult createUser( MatchPredictions predictions ) {

		AuthenticationResult result = new AuthenticationResult();
		
		String community = (String) httpRequest.getAttribute("community");

		User user = dao.findExistingUser(community, predictions.getEmail() );
		if (user == null) {
			dao.insert( predictions.getEmail(), predictions.getName(), community, predictions.getPassword(), false );
			savePredictions( community, predictions.getEmail(), predictions );
			String authToken = generateAuthToken(community, predictions.getEmail(), predictions.getPassword());
			result.setAuthToken( authToken );
		} else {
			result.setMessage("Un utilisateur avec cet email existe d�j�");
		}

		return result;
	}

	@POST
	@Path("/create")
	public void createUser(@FormParam("email") String email, @FormParam("name") String name, @FormParam("password") String password, @FormParam("admin") boolean admin) {
		if (admin) {
			// check that creator is admin
		}
		String community = (String) httpRequest.getAttribute("community");
		dao.insert(email, name, community, password, admin);
	}
	
	@GET
	@Path("/availability")
	public boolean isAvailable(@QueryParam("email") String email) {
		String community = (String) httpRequest.getAttribute("community");
		return dao.findExistingUser(community, email) == null;
	}
	
	@GET
	@Path("/create")
	@Produces(MediaType.TEXT_HTML)
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
	public void save( @Auth User user, MatchPredictions predictions ) {
		savePredictions( user.getCommunity(), user.getEmail(), predictions );
	}

	@GET
	@Path("/list")
	public Rankings getRankings() {
		String community = (String) httpRequest.getAttribute("community");
		return new Rankings( dao.findUsersOrderedByScore( community ) );
	}	
	
	@GET
	@Path("/predictions")
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
	public SigninPageView signIn() {
		return new SigninPageView();
	}

	@GET
	@Path("/forget-password")
	@Produces(MediaType.TEXT_HTML)
	public ForgetPasswordPageView forgetPasswordGetView() {
		return new ForgetPasswordPageView();
	}

	@POST
	@Path("/forget-password")
	@Produces(MediaType.TEXT_HTML)
	public ForgetPasswordPageView forgetPassword( @FormParam("email") String email ) {
		String community = (String) httpRequest.getAttribute("community");
		UUID uuid = UUID.randomUUID();
		dao.setChangePasswordToken(community, email, uuid);
		return new ForgetPasswordPageView();
	}

	@POST
	@Path("/signin")
	public MatchPredictions signIn( @FormParam("email") String email, @FormParam("password") String password ) {

		String community = (String) httpRequest.getAttribute("community");
		
		MatchPredictions predictions = null;
		if( dao.authentify(community, email.toLowerCase().trim(), password) != null ) {
			predictions = buildPredictions( community, email );
			predictions.setAuthToken( generateAuthToken(community, email, password) );
		}

		return predictions;
	}
}
