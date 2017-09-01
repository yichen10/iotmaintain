package com.ztesoft.iot.maintain.factory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.inter.ServletInter;
import com.ztesoft.iot.maintain.inter.imp.QueryHostName;
import com.ztesoft.iot.maintain.inter.imp.QueryPortName;
import com.ztesoft.iot.maintain.inter.imp.QueryTimeName;
import com.ztesoft.iot.maintain.inter.imp.QueryVerticalTreeGrid;
import com.ztesoft.iot.maintain.inter.imp.UpdateFileContent;
import com.ztesoft.iot.maintain.inter.imp.InterDefault;
import com.ztesoft.iot.maintain.inter.imp.QueryAppName;
import com.ztesoft.iot.maintain.inter.imp.QueryFileContent;
import com.ztesoft.iot.maintain.inter.imp.QueryFileName;

public class ServiceFactory {
	public static ServletInter doService(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) {
		String type = request.getRequestURI();
		type = type.substring(type.lastIndexOf('/') + 1);
		if ("QueryHostName.do".equals(type)) {
			return new QueryHostName(response, request);
		} else if ("QueryAppName.do".equals(type)) {
			return new QueryAppName(response, request);
		} else if ("QueryPortName.do".equals(type)) {
			return new QueryPortName(response, request);
		} else if ("QueryFileName.do".equals(type)) {
			return new QueryFileName(response, request);
		} else if ("QueryTimeName.do".equals(type)) {
			return new QueryTimeName(response, request);
		} else if ("QueryFileContent.do".equals(type)) {
			return new QueryFileContent(response, request);
		} else if ("QueryVerticalTreeGrid.do".equals(type)) {
			return new QueryVerticalTreeGrid(response, request);
		} else if("UpdateFileContent.do".equals(type)){
			return new UpdateFileContent(response, request);
		} else {
			return new InterDefault(response, type);
		}
	}

}
