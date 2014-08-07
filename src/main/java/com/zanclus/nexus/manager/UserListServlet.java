package com.zanclus.nexus.manager;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class UserListServlet extends AccessControlledServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8057636122856402469L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (isAdmin(req)) {
			try (	OutputStream out = resp.getOutputStream();
					Connection c = ds.getConnection();
					Statement s = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					ResultSet r = s.executeQuery("SELECT DISTINCT user FROM token")) {
				r.beforeFirst();
				JsonObject result = new JsonObject();
				JsonArray users = new JsonArray();
				while (r.next()) {
					JsonPrimitive user = new JsonPrimitive(r.getString(1));
					users.add(user);
				}
				result.add("users", users);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setHeader("Content-Type", "application/json");
				out.write(result.toString().getBytes());
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}
}
