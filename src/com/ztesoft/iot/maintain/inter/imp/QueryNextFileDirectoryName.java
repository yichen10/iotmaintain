package com.ztesoft.iot.maintain.inter.imp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.ztesoft.iot.maintain.inter.ServletInter;
import com.ztesoft.iot.maintain.readconfig.ReadCon;
import com.ztesoft.iot.maintain.utils.ExecuteCommand;

import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QueryNextFileDirectoryName implements ServletInter {

	private HttpServletResponse response;
	private HttpServletRequest request;

	public QueryNextFileDirectoryName(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONArray json = new JSONArray();
		try {
			String host = request.getParameter("host");
			String fileDirectory = request.getParameter("filedirectory");
			
			SSHExec ssh = ExecuteCommand.connect(host, ReadCon.USERNAME, ReadCon.PASSWORD);
			
			CustomTask ct1 = null;
			if (StringUtils.isEmpty(fileDirectory)) {
				ct1 = new ExecCommand("ls / ");
			}else {
				ct1 = new ExecCommand("ls " + fileDirectory);
			}
			Result res2 = ssh.exec(ct1);
			if (res2.isSuccess) {
				System.out.println("sysout: " + res2.sysout);
				String[] resOut = res2.sysout.split("\n");
				for (int j = 0; j < resOut.length; j++) {
					JSONObject jsonobj = new JSONObject();
					jsonobj.put("value", "/" +resOut[j]);
					json.add(jsonobj);
				}
			} else {
				System.err.println("Return code: " + res2.rc);
				System.err.println("error message: " + res2.error_msg);
			}
			CustomTask ct3 = new ExecCommand("whoami");
			Result res3 = ssh.exec(ct3);
			if (res2.isSuccess) {
				System.out.println("Return code: " + res3.rc);
				System.out.println("sysout: " + res3.sysout);
			} else {
				System.err.println("Return code: " + res3.rc);
				System.err.println("error message: " + res3.error_msg);
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
	
}
