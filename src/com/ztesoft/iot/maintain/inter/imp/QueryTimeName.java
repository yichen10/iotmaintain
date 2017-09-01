package com.ztesoft.iot.maintain.inter.imp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryTimeName implements ServletInter {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DbOperate dbo = new DbOperate();

	public QueryTimeName(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONArray json = new JSONArray();
		try {
			String host = request.getParameter("host");
			String app = request.getParameter("app");
			String port = request.getParameter("port");
			String file = request.getParameter("file");
			List<Object[]> list = dbo.search("select file_change_date from monitor_file_log where host_ip = ? and app_name = ? and host_port = ? and file_name = ? order by file_change_date", host, app, port, file);
			for(Object[] objs : list){
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("value", dateToString((Date)objs[0]));
				json.add(jsonobj);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
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
	
	private String dateToString(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
