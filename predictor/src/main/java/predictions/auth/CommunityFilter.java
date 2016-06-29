package predictions.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CommunityFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String community = extractCommunity( request.getServerName() );
		request.setAttribute("community", community);
		chain.doFilter(request, response);

	}

	public static String extractCommunity(String community) {
		int i = community.indexOf('.');
		if (i > 0) {
			community = community.substring(0, i);
		}
		
		if (community.equals("localhost")) {
			return "grand-est";
		}
		
		return community;
	}

	public void destroy() {
		
	}

}
