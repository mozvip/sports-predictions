package predictions.phases;

import java.io.IOException;

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
	
	private final static Logger logger = LoggerFactory.getLogger( PhaseFilter.class );

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// get current phase
		Phase currentPhase = PhaseManager.getInstance().getCurrentPhase();
		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String requestURI = servletRequest.getRequestURI();
		
		String community = (String) request.getAttribute("community");
		if (community.equals("test")) {
			chain.doFilter(request, response);
			return;
		}
		
		if ((requestURI.endsWith("/") || requestURI.endsWith(".html")) && ! ( requestURI.startsWith( currentPhase.getWelcomePage() ))) {
			HttpServletResponse servletResponse = (HttpServletResponse) response;
			servletResponse.sendRedirect( currentPhase.getWelcomePage());
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

}
