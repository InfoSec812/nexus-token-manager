package com.zanclus.nexus.manager;

import static javax.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class UserListServlet extends AccessControlledServlet {

	private static final Logger LOG = LoggerFactory.getLogger(UserListServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8057636122856402469L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOG.info("Got user list request.");
		if (isAdmin(req)) {
			LOG.info("User is admin!");
			try (	OutputStream out = resp.getOutputStream();
					Connection c = ds.getConnection();
					Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet r = s.executeQuery("SELECT DISTINCT nexususer FROM tokens")) {
				r.beforeFirst();
				JsonObject result = new JsonObject();
				JsonArray users = new JsonArray();
				while (r.next()) {
					LOG.info("Adding User: "+r.getString(1));
					JsonPrimitive user = new JsonPrimitive(r.getString(1));
					users.add(user);
				}
				result.add("users", users);
				resp.setStatus(SC_OK);
				resp.setHeader("Content-Type", "application/json");
				out.write(result.toString().getBytes());
			} catch (Exception e) {
				resp.setStatus(SC_INTERNAL_SERVER_ERROR);
				throw new ServletException(e);
			}
		} else {
			resp.setStatus(SC_UNAUTHORIZED);
			LOG.info("User is NOT ADMIN");
		}
	}
}
