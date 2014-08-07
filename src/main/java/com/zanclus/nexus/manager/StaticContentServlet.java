package com.zanclus.nexus.manager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class StaticContentServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7883666352724679021L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream is =this.getClass().getResourceAsStream("classpath:"+req.getRequestURI());
		if (is==null) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			resp.getOutputStream().close();
		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setHeader("Content-Type", URLConnection.guessContentTypeFromName(req.getRequestURI()));
			BufferedInputStream bis = new BufferedInputStream(is);
			OutputStream out = resp.getOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len=bis.read(buffer))>=0) {
				out.write(buffer, 0, len);
			}
			out.close();
		}
	}
}
