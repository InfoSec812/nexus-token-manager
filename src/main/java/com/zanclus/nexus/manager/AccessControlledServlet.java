package com.zanclus.nexus.manager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public abstract class AccessControlledServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4326841583748036621L;

	protected DataSource ds = null;

	public AccessControlledServlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context)initCtx.lookup("java:/comp/env");
			ds = (DataSource)envCtx.lookup("jdbc/TokenDS");
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	protected boolean isAdmin(HttpServletRequest req) {
		if (	req.getSession()!=null && 
				req.getSession().getAttribute("authenticate")!=null &&
				(Boolean)req.getSession().getAttribute("authenticated")) {
			if (req.getSession().getAttribute("is_admin")!=null) {
				return (Boolean)req.getSession().getAttribute("is_admin");
			}
		}
		return false;
	}

	protected boolean isAuthorized(HttpServletRequest req, String sessionUser, String requestedUser) {
		if (req.getSession()!=null && req.getSession().getAttribute("is_admin")!=null && ((Boolean)req.getSession().getAttribute("is_admin"))) {
			return true;
		} else {
			if (sessionUser!=null && requestedUser!=null && sessionUser.contentEquals(requestedUser)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isAuthorized(HttpServletRequest req) {
		if (	req.getSession()!=null && 
				req.getSession().getAttribute("authenticate")!=null &&
				(Boolean)req.getSession().getAttribute("authenticated")) {
			String user = req.getParameter("username");
			String loggedInUser = (String) req.getSession().getAttribute("username");
			Boolean isAdmin = (Boolean)req.getSession().getAttribute("is_admin");
			if (user.contentEquals(loggedInUser) || isAdmin==Boolean.TRUE) {
				return true;
			}
		}
		return false;
	}

}