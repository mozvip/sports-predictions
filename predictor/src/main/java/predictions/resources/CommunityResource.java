package predictions.resources;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import predictions.model.db.AccessType;
import predictions.model.db.Community;
import predictions.model.db.CommunityDAO;
import predictions.model.db.User;

@Path("/community")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api
public class CommunityResource {
	
	@Context private HttpServletRequest httpRequest;
	
	private CommunityDAO communityDAO;
	
	public CommunityResource( CommunityDAO communityDAO ) {
		this.communityDAO = communityDAO;
	}
	
	@GET
	@ApiOperation(tags="public", value = "Retrieves information about the community")
	public Community getCommunity() {
		String name = (String) httpRequest.getAttribute("community");
		Community community = communityDAO.getCommunity(name);
		if (community == null) {
			community = new Community(name, true, AccessType.R, AccessType.R);
		}
		return community;
	}
	
	@POST
	@RolesAllowed("ADMIN")
	@ApiOperation(tags="admin", value = "Update settings of the community", authorizations = @Authorization("basicAuth"))
	public void save(@ApiParam(hidden = true) @Auth User user, Community communityParameters) {
		String name = (String) httpRequest.getAttribute("community");
		communityDAO.updateCommunity(name, communityParameters.isCreateAccountEnabled(), communityParameters.getGroupsAccess(), communityParameters.getFinalsAccess());
	}

}
