package predictions.phases;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhaseFilter implements Filter {
	
	private final static Logger logger = LoggerFactory.getLogger( PhaseFilter.class );

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// get current phase
		Phase currentPhase = PhaseManager.getInstance().getCurrentPhase();
		
		chain.doFilter(request, response);
	}

	public void destroy() {
		
	}

}
