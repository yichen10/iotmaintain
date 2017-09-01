package com.ztesoft.iot.maintain.inter.imp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryHostName implements ServletInter {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DbOperate dbo = new DbOperate();

	public QueryHostName(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONArray json = new JSONArray();
		try {
			List<Object[]> list = dbo.search("select * from (select host_ip from monitor_file_log group by host_ip) order by host_ip");
			for(Object[] objs : list){
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("value", objs[0]);
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
}
