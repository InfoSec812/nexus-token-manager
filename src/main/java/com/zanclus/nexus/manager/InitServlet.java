package com.zanclus.nexus.manager;

import static java.sql.ResultSet.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An unmapped servlet which loads at startup and configures the enviroment
 * 
 * @author <a href="mailto: deven.phillips@sungardas.com">Deven Phillips</a>
 *
 */
public class InitServlet implements Servlet {

	private static final String CREATE_TOKEN_TABLE = "CREATE TABLE tokens ("
			+ "id BIGINT IDENTITY, "
			+ "token VARCHAR(50) NOT NULL, "
			+ "username VARCHAR(50) NOT NULL);";

	private static final Logger LOG = LoggerFactory.getLogger(InitServlet.class);

	private static final String TABLE_EXISTS = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME='TOKENS'";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("InitServlet is loading.");
		DataSource ds;
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:/comp/env");
			ds = (DataSource)envCtx.lookup("jdbc/TokenDS");
			LOG.warn("Got Datasource from context");
			envCtx.close();
			initCtx.close();
			try (	Connection c = ds.getConnection();
					Statement s = c.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
					ResultSet r = s.executeQuery(TABLE_EXISTS)) {
				if (r.first()) {
					LOG.debug("Tokens table exists in database.");
					return;
				}
			} catch (Exception e) {
				LOG.error("Error verifying existence of token table", e);
				throw new ServletException(e);
			}

			try (	Connection c = ds.getConnection();
					Statement s = c.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);
					ResultSet r = s.executeQuery(CREATE_TOKEN_TABLE)) {
				return;
			} catch (Exception e) {
				LOG.error("Error creating token table.", e);
				throw new ServletException(e);
			}
		} catch (NamingException ne) {
			LOG.error("Unable to retrieve datasource from JNDI namespace", ne);
		}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#getServletConfig()
	 */
	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#getServletInfo()
	 */
	@Override
	public String getServletInfo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:/comp/env");
			BasicDataSource ds = (BasicDataSource)envCtx.lookup("jdbc/TokenDS");
			ds.close();
		} catch(NamingException | SQLException e) {
			LOG.error("Error destroy InitServlet", e);
		}
	}

}
