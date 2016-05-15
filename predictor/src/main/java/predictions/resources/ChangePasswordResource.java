package predictions.resources;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
	@ApiOperation(value="Used when a connected user changes his own password")
	@Path("/self")
	public void changePassword( @Auth User user, @FormParam("password") String oldPassword, @FormParam("password") String newPassword) {
		oldPassword = oldPassword.trim();
		newPassword = newPassword.trim();
		if (userDAO.authentify( user.getCommunity(), user.getEmail(), oldPassword ) != null) {
			userDAO.updatePassword( user.getEmail(), user.getCommunity(), newPassword );
			logger.info(String.format("User %s changed his password", user.getEmail() ));
		} else {
			logger.warn(String.format("User %s tries to change his password but input the wrong old password", user.getEmail() ));	
		}
	}

	@POST
	@ApiOperation(value="Used when a user had forgotten his password and will now set it")
	@Path("/reset")
	public void changePassword( @FormParam("email") String email, @FormParam("changePasswordToken") String changePasswordToken, @FormParam("password") String password ) {
		email = email.trim().toLowerCase();
		password = password.trim();
		String community = (String) httpRequest.getAttribute("community");
		userDAO.updatePassword(email, community, password);
		logger.info(String.format("User %s changed his password", email));
	}

	@POST
	@ApiOperation(value="Used when an admin changes the password for another user in the community")
	@RolesAllowed("ADMIN")
	@Path("/admin")
	public void changePassword( @Auth User user, @FormParam("password") String password, @FormParam("email") String email, @FormParam("changePasswordToken") String changePasswordToken) {
		email = email.trim().toLowerCase();
		password = password.trim();
		logger.info(String.format("Admin %s changed the password of %s", user.getEmail(), email));
		
		// purposedly we use here the admin's community, just in case an admin is trying to change the password of someone he shouldn't
		userDAO.updatePassword(email, user.getCommunity(), password);
	}

}
