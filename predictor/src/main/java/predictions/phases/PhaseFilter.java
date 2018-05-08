package predictions.phases;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import predictions.model.db.Community;
import predictions.model.db.CommunityDAO;

public class PhaseFilter implements Filter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( PhaseFilter.class );

	private PhaseManager phaseManager;
	private CommunityDAO communityDAO;

	public PhaseFilter(PhaseManager phaseManager, CommunityDAO communityDAO) {
		this.phaseManager = phaseManager;
		this.communityDAO = communityDAO;
	}

	public void init(FilterConfig filterConfig) {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// get current phase
		Phase currentPhase = phaseManager.getCurrentPhase();
		if (currentPhase.getWelcomePage() == null) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String requestURI = servletRequest.getRequestURI();
		
		String communityName = (String) request.getAttribute("community");

		// TODO: cache communities
		Community community = communityDAO.getCommunity(communityName);

		if (communityName.equals("test")||  communityName.equals("localhost")) {
			chain.doFilter(request, response);
			return;
		}
		
		if ((requestURI.endsWith("/") || requestURI.endsWith(".html")) && ! ( requestURI.startsWith( currentPhase.getWelcomePage() ))) {
			HttpServletResponse servletResponse = (HttpServletResponse) response;
			LOGGER.info("Redirecting request {} to {}", requestURI, currentPhase.getWelcomePage() );
			String url = String.format("https://%s%s?phaseEnd=%s", request.getServerName(), currentPhase.getWelcomePage(), new SimpleDateFormat("yyyy/MM/dd").format(currentPhase.getPhaseEnd()));
			servletResponse.sendRedirect(url);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

}
