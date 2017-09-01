package com.ztesoft.iot.maintain.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbUtil;
import com.ztesoft.iot.maintain.factory.ServiceFactory;
import com.ztesoft.iot.maintain.inter.ServletInter;

public class BaseServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		try {
			System.out.println(DbUtil.getJNDI());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
//		response.setHeader("Content-Type", "text/html; charset=utf-8");
		ServletInter inter = ServiceFactory.doService(request, response, this.getServletContext());
		inter.doService();
	}

}
