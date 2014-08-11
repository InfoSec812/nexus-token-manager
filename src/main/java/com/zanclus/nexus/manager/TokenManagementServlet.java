package com.zanclus.nexus.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class TokenManagementServlet extends AccessControlledServlet {

	private static final Logger LOG = LoggerFactory.getLogger(InitServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1112459822963782524L;

	@Override   // READ existing token(s) for given user(s) while checking authorizations
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String sessionUser = null;
		if (req.getSession()!=null &&
				req.getSession().getAttribute("username")!=null) {
			sessionUser = (String) req.getSession().getAttribute("username");
		}
		String reqUser = null;
		if (req.getParameter("username")!=null) {
			reqUser = req.getParameter("username");
		}
		if (isAuthorized(req, sessionUser, reqUser)) {
			String user = (String) (reqUser==null?req.getSession().getAttribute("username"):reqUser);
			if (user==null) {
				user = sessionUser;
			}
			try (	OutputStream out = resp.getOutputStream();
					Connection c = ds.getConnection();
					PreparedStatement s = c.prepareStatement("SELECT id, token FROM tokens WHERE username=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				s.setString(1, user);
				ResultSet r = s.executeQuery();
				r.beforeFirst();
				JsonObject result = new JsonObject();
				JsonArray tokens = new JsonArray();
				while (r.next()) {
					JsonObject token = new JsonObject();
					token.addProperty("id", r.getString(1));
					token.addProperty("token", r.getString(2));
					tokens.add(token);
				}
				result.add("tokens", tokens);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("Content-Type", "application/json");
				out.write(result.toString().getBytes());
				out.close();
				r.close();
			} catch (SQLException e) {
				throw new ServletException(e);
			}
		} else {
			LOG.error("User '"+sessionUser+"' forbidden from accessing data for user '"+reqUser+"'");
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Override	// DELETE the specified token IF authorized
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (isAuthorized(req)) {
			String user = req.getParameter("username");
			String token = req.getParameter("token");
			try (	OutputStream out = resp.getOutputStream();
					Connection c = ds.getConnection();
					Statement s = c.createStatement()) {
				s.executeUpdate("DELETE FROM tokens WHERE token='"+token+"' AND username='"+user+"'");
				JsonObject result = new JsonObject();
				result.addProperty("username", user);
				result.addProperty("token", token);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("Content-Type", "application/json");
				out.write(result.toString().getBytes());
				out.close();
			} catch (SQLException e) {
				throw new ServletException(e);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Override	// CREATE a new token for the given user IF authorized
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String sessionUser = null;
		if (req.getSession()!=null &&
				req.getSession().getAttribute("username")!=null) {
			sessionUser = (String) req.getSession().getAttribute("username");
		}
		String reqUser = null;
		if (req.getParameter("username")!=null) {
			reqUser = req.getParameter("username");
		}
		if (isAuthorized(req, sessionUser, reqUser)) {
			String user = (String) (reqUser==null?req.getSession().getAttribute("username"):reqUser);
			if (user==null) {
				user = sessionUser;
			}
			String token = UUID.randomUUID().toString();
			try (	OutputStream out = resp.getOutputStream();
					Connection c = ds.getConnection();
					Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				s.executeUpdate("INSERT INTO tokens (username, token) VALUES ('"+user+"', '"+token+"')");
				JsonObject result = new JsonObject();
				result.addProperty("username", user);
				result.addProperty("token", token);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("Content-Type", "application/json");
				out.write(result.toString().getBytes());
				out.close();
			} catch (SQLException e) {
				throw new ServletException(e);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		}
	}
}
