package com.ztesoft.iot.maintain.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.ztesoft.iot.maintain.readconfig.ReadCon;

public class DbUtil {

	private static String jndiname;
	
	static {
		jndiname = ReadCon.getJndiName();
	}
	
	public static Connection getJNDI() throws Exception{
		Connection conn = null;
		try {
			Context context = new InitialContext();
			DataSource ds = (DataSource) context.lookup(jndiname);
			conn = ds.getConnection();
		} catch (Exception e) {
			conn = null;
			throw new Exception(e.getMessage());
		}
		return conn;
	}


	public static void release(Statement stmt, Connection con) {
		release(stmt);
		release(con);
	}

	public static void release(ResultSet rs, Statement stmt, Connection con) {
		release(rs);
		release(stmt, con);
	}
	
	public static void release(ResultSet rs, Statement stmt){
		release(rs);
		release(stmt);
	}
	
	public static void release(ResultSet rs){
		try {
			if(rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void release(Statement stmt){
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void release(Connection con){
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}