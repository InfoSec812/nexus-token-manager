package com.zanclus.nexus.auth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class RemoteUserTokenFilter implements Filter {

	private String validationUrl = null;

	public void init(FilterConfig filterConfig) throws ServletException {
		validationUrl = filterConfig.getInitParameter("validation_url");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (HttpServletRequest.class.isInstance(request)) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
			final HttpServletResponse httpResponse = (HttpServletResponse) response;

			String token = httpRequest.getHeader("AUTH_TOKEN");
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(validationUrl);
			get.setQueryString("token="+token);
			int code = client.executeMethod(get);
			if (code==200) {
				String username = get.getResponseHeader("REMOTE_USER").getValue();
				httpRequest.setAttribute("username", username);
			} else {
				httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
				httpResponse.getOutputStream().close();
			}

			HttpServletRequestWrapper req = new HttpServletRequestWrapper(
					(HttpServletRequest) httpRequest) {
				@Override
				public String getHeader(String name) {
					if (name.toLowerCase().contentEquals("remote_user")) {
						if (super.getAttribute("username")!=null) {
							return (String) super.getAttribute("username");
						} else {
							return null;
						}
					}
					return super.getHeader(name);
				}

				@Override
						public Enumeration<String> getHeaderNames() {
							Vector<String> headerNames = new Vector<String>();
							Enumeration<String> hdrNames ;
							hdrNames = super.getHeaderNames();
							while (hdrNames.hasMoreElements()) {
								headerNames.add(hdrNames.nextElement());
							}
							headerNames.add("REMOTE_USER");
							return headerNames.elements();
						}
			};
			chain.doFilter(req, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		// NO-OP
	}

}
