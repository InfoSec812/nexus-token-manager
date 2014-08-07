package com.zanclus.nexus.manager;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class UserInfoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9199983622960300840L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String nexusAccount = (String) req.getSession().getAttribute("nexusAccount");
		if (nexusAccount!=null) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setHeader("Content-Type", "application/json");
			OutputStream out = resp.getOutputStream();
			out.write(nexusAccount.getBytes());
			out.close();
		}
	}
}
