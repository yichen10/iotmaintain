package com.ztesoft.iot.maintain.inter.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;

import net.sf.json.JSONObject;

public class QueryFileContent implements ServletInter {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DbOperate dbo = new DbOperate();

	public QueryFileContent(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONObject json = new JSONObject();
		StringBuffer content = null;
		try {
			String host = request.getParameter("host");
			String app = request.getParameter("app");
			String port = request.getParameter("port");
			String file = request.getParameter("file");
			String time = request.getParameter("time");
			List<Object[]> list = dbo.search("select file_content from monitor_file_log where host_ip = ? and app_name = ? and host_port = ? and file_name = ? and file_change_date = to_date(?,'yyyy-mm-dd hh24:mi:ss')", host, app, port, file, time);
			Clob clob = null;
			if(list.size() > 0){
				clob = (Clob)list.get(0)[0];
			}
			if(clob != null){
				content = clobToString(clob);
				json.put("code", "0000");
				json.put("content", content.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\r\n", "<br>"));
			}else{
				json.put("code", "1000");
				json.put("message", "数据空！");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			json.put("code", "1000");
			json.put("message", "请求数据异常！");
			e1.printStackTrace();
		}
		try {
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			PrintWriter pw = response.getWriter();
			pw.write(json.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public StringBuffer clobToString(Clob clob) throws SQLException, IOException {
		Reader is = clob.getCharacterStream();
		BufferedReader br = new BufferedReader(is);
		StringBuffer sb = new StringBuffer();
		String s = null;
		while ((s = br.readLine()) != null) {
			sb.append(s).append("\r\n");
		}
		br.close();
		is.close();
		return sb;
	}
}
