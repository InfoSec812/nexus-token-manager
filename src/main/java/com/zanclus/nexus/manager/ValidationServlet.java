package com.zanclus.nexus.manager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class ValidationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2439429207829014866L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			Context initContext = new InitialContext();
			Context envCtx = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envCtx.lookup("jdbc/TokenDS");
			Connection c = ds.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT username FROM tokens WHERE token=?", ResultSet.CONCUR_READ_ONLY);
			s.setString(1, req.getParameter("token"));
			ResultSet r = s.executeQuery();
			String username = null;
			if (r.first()) {
				username = r.getString(1);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("REMOTE_USER", username);
				resp.getOutputStream().close();
			} else {
				resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resp.getOutputStream().close();
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}
}
