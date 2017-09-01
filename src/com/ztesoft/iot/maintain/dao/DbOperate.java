package com.ztesoft.iot.maintain.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbOperate {
	
	public int update(String sql,Object...params) throws Exception{
		Connection con = DbUtil.getJNDI();
		int flag = this.update(sql, con, params);
		DbUtil.release(con);
		return flag;
	}
	
	public int update(String sql,Connection con,Object...paras){
		int count = 0;
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				stmt.setObject(i+1, paras[i]);
			}
			count = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	
	public List<Object[]> search(String sql,Object...paras) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>(); 
		Connection con = DbUtil.getJNDI();
		list=this.search(sql, con, paras);
		try {
			if(con != null){
				con.close();
			}
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
		return list;
	}
	
	public List<Object[]> search(String sql,Connection con,Object...paras) throws Exception{
		List<Object[]> list = new ArrayList<Object[]>(); 
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(sql);
			for(int i = 0;i<paras.length;i++){
				stmt.setObject(i+1, paras[i]);
			}
			rs = stmt.executeQuery();
			int size = rs.getMetaData().getColumnCount();
			while(rs.next()){
				Object[] obj = new Object[size];
				for(int i=0;i<size;i++){
					obj[i] = rs.getObject(i+1);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			DbUtil.release(rs, stmt);
		}
		return list;
	}
	
	public void transactionManagementCon(Connection con, ArrayList<Object[]> listParas) {
		int i = 0;
		PreparedStatement stmt = null;
		try {
			con.setAutoCommit(false);
			for(Object[] objs : listParas){
				stmt = con.prepareStatement(String.valueOf(objs[i++]));
				while(i < objs.length){
					stmt.setObject(i-1, objs[i++]);
				}
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.release(stmt);
		}
	}
	
}