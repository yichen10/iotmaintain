package com.ztesoft.iot.maintain.inter.imp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.inter.ServletInter;

import net.sf.json.JSONArray;

public class InterDefault implements ServletInter {
	private HttpServletResponse response;
	private String type;

	public InterDefault(HttpServletResponse response, String type) {
		this.response = response;
		this.type = type;
	}

	@Override
	public void doService() {
		System.out.println("URI错误！没有“" + type + "”这个请求类型");
		JSONArray json = new JSONArray();
		json.add(0, "error");
		json.add(1, "URI错误！没有“" + type + "”这个请求类型");
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
