package predictions.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.model.User;
import predictions.model.UserDAO;
import predictions.views.ChangePasswordView;

@Path("/change-password")
@Produces(MediaType.TEXT_HTML)
public class ChangePasswordResource {
	
	private final static Logger logger = LoggerFactory.getLogger( ChangePasswordResource.class );
	
    private UserDAO userDAO;
	
	public ChangePasswordResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Context private HttpServletRequest httpRequest;

	@GET
	@ApiOperation(value="Displays the view to change password", hidden=true)
	public ChangePasswordView getView() {
		return new ChangePasswordView( userDAO, (String) httpRequest.getAttribute("community") );
	}
	
	@POST
	@ApiOperation(value="Changes a password")
	public void changePassword( @Auth User user, @FormParam("password") String password, @FormParam("email") String email, @FormParam("changePasswordToken") String changePasswordToken) {
		email = email.trim().toLowerCase();
		if (user.isAdmin() || StringUtils.equalsIgnoreCase(user.getEmail(), email)) {
			password = password.trim();
			logger.info(String.format("User %s changing password for %s", user.getEmail(), email));
			userDAO.updatePassword(email, user.getCommunity(), password);
		} else {
			logger.warn(String.format("Non-admin user %s attempted to change password for %s", user.getEmail(), email));
		}
	}
	
}
