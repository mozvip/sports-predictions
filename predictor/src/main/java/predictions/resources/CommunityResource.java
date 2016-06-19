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
import predictions.model.Community;
import predictions.model.CommunityDAO;
import predictions.model.User;

@Path("/community")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommunityResource {
	
	@Context private HttpServletRequest httpRequest;
	
	private CommunityDAO communityDAO;
	
	public CommunityResource( CommunityDAO communityDAO ) {
		this.communityDAO = communityDAO;
	}
	
	@GET
	public Community getCommunity() {
		String name = (String) httpRequest.getAttribute("community");
		Community community = communityDAO.getCommunity(name);
		if (community == null) {
			community = new Community(name, true, true, false);
		}
		return community;
	}
	
	@POST
	@RolesAllowed("ADMIN")
	public void save(@Auth User user, Community communityParameters) {
		String name = (String) httpRequest.getAttribute("community");
		communityDAO.updateCommunity(name, communityParameters.isCreateAccountEnabled(), communityParameters.isGroupsEditEnabled(), communityParameters.isFinalsEditEnabled());
	}

}
