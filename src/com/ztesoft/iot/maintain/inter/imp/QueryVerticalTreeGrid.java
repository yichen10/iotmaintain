package com.ztesoft.iot.maintain.inter.imp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryVerticalTreeGrid implements ServletInter {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private DbOperate dbo = new DbOperate();

	public QueryVerticalTreeGrid(HttpServletResponse response, HttpServletRequest request) {
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
			List<Object[]> list = dbo.search("select host_ip,host_port,file_change_date from monitor_file_log where app_name = ? and file_name = ? order by file_change_date", app, file);
			Map<String, List<Object[]>> getHost = new HashMap<String, List<Object[]>>();
			for(Object[] objs : list){
				String temp = (String) objs[0];
				if(temp.equals(host) && ((String)objs[1]).equals(port)){
					continue;
				}
				if(!getHost.containsKey(temp)){
					getHost.put(temp, new ArrayList<Object[]>());
				}
				getHost.get(temp).add(objs);
			}
			int k = 0;
			for(List<Object[]> listObj : getHost.values()){
				JSONObject getPort = new JSONObject();
				JSONArray jsonPort = new JSONArray();
				Map<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
				String host2 = (String) listObj.get(0)[0];
				String hostId = "A" + k;
				for(Object[] objs : listObj){
					String temp = (String)objs[1];
					if(!map.containsKey(temp)){
						map.put(temp, new ArrayList<Object[]>());
					}
					map.get(temp).add(objs);
				}
				int j = 0;
				for(List<Object[]> Listobjs : map.values()){
					JSONArray jsonTime = new JSONArray();
					JSONObject jport = new JSONObject();
					String portId = hostId + "B" + j;
					int i = 0;
					for(Object[] objs : listObj){
						JSONObject jobj = new JSONObject();
						jobj.put("id", portId + "C" + i);
						jobj.put("text", dateToString((Date)objs[2]));
						jsonTime.add(jobj);
					}
					jport.put("id", portId);
					jport.put("text", Listobjs.get(0)[1]);
					jport.put("children", jsonTime);
					jsonPort.add(jport);
				}
				getPort.put("id", hostId);
				getPort.put("text", host2);
				getPort.put("children", jsonPort);
				json.add(getPort);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			PrintWriter pw = response.getWriter();
			pw.write(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String dateToString(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
