package predictions.resources;

import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.ApiOperation;
import predictions.model.db.User;
import predictions.model.db.UserDAO;

@Path("/change-password")
@Produces(MediaType.APPLICATION_JSON)
@Api
public class ChangePasswordResource {
	
	private final static Logger logger = LoggerFactory.getLogger( ChangePasswordResource.class );
	
    private UserDAO userDAO;
	
	public ChangePasswordResource(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Context private HttpServletRequest httpRequest;

	@POST
	@ApiOperation(value="Used when a connected user changes his own password")
	@Path("/self")
	public Response changePassword( @Auth User user, @NotNull @FormParam("oldPassword") String oldPassword, @NotNull @FormParam("newPassword") String newPassword) {
		oldPassword = oldPassword.trim();
		newPassword = newPassword.trim();
		if (userDAO.authentify( user.getCommunity(), user.getEmail(), oldPassword ) != null) {
			userDAO.updatePassword( user.getEmail(), user.getCommunity(), newPassword );
			logger.info(String.format("User %s changed his password", user.getEmail() ));
			
			return Response.ok().build();
		} else {
			logger.warn(String.format("User %s tries to change his password but input the wrong old password", user.getEmail() ));
			
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@ApiOperation(value="Used when a user had forgotten his password and will now set it")
	@Path("/reset")
	public Response changePassword( @FormParam("email") String email, @FormParam("changePasswordToken") String changePasswordToken, @FormParam("password") String password ) {
		email = email.trim().toLowerCase();
		password = password.trim();
		String community = (String) httpRequest.getAttribute("community");
		userDAO.updatePassword(email, community, password);
		logger.info(String.format("User %s changed his password", email));
		userDAO.setChangePasswordToken(community, email, UUID.randomUUID());
		
		return Response.ok().build();
	}

	@POST
	@ApiOperation(value="Used when an admin changes the password for another user in the community")
	@RolesAllowed("ADMIN")
	@Path("/admin")
	public Response changePassword( @Auth User user, @FormParam("password") String password, @FormParam("email") String email, @FormParam("changePasswordToken") String changePasswordToken) {
		email = email.trim().toLowerCase();
		password = password.trim();
		logger.info(String.format("Admin %s changed the password of %s", user.getEmail(), email));
		
		// purposedly we use here the admin's community, just in case an admin is trying to change the password of someone he shouldn't
		if (userDAO.updatePassword(email, user.getCommunity(), password) < 0) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		return Response.ok().build();
	}

}
