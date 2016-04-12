package predictions.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import predictions.model.ActualResult;
import predictions.model.ActualResultDAO;

@Path("/matches")
@Produces(MediaType.APPLICATION_JSON)
public class MatchesResource {
	
	private ActualResultDAO actualResultDAO;

	public MatchesResource(ActualResultDAO actualResultDAO) {
		super();
		this.actualResultDAO = actualResultDAO;
	}

	@GET
	public List<ActualResult> getMatches() {
		return actualResultDAO.findValidated();
	}

}
