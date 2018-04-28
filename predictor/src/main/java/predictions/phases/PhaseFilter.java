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

public class PhaseFilter implements Filter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger( PhaseFilter.class );

	private PhaseManager phaseManager;

	public PhaseFilter(PhaseManager phaseManager) {
		this.phaseManager = phaseManager;
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
		
		String community = (String) request.getAttribute("community");
		if (community.equals("test")||  community.equals("localhost")) {
			chain.doFilter(request, response);
			return;
		}
		
		if ((requestURI.endsWith("/") || requestURI.endsWith(".html")) && ! ( requestURI.startsWith( currentPhase.getWelcomePage() ))) {
			HttpServletResponse servletResponse = (HttpServletResponse) response;
			LOGGER.info("Redirecting request {} to {}", requestURI, currentPhase.getWelcomePage() );
			String url = String.format("%s?phaseEnd=%s", currentPhase.getWelcomePage(), new SimpleDateFormat("yyyy/MM/dd").format(currentPhase.getPhaseEnd()));
			servletResponse.sendRedirect(url);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

}
