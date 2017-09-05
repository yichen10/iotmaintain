package com.ztesoft.iot.maintain.inter.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ztesoft.iot.maintain.dao.DbOperate;
import com.ztesoft.iot.maintain.inter.ServletInter;
import com.ztesoft.iot.maintain.readconfig.ReadCon;
import com.ztesoft.iot.maintain.utils.ExecuteCommand;

import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;
import net.sf.json.JSONObject;

public class QueryFileDirectory implements ServletInter {

	private HttpServletResponse response;
	private HttpServletRequest request;

	public QueryFileDirectory(HttpServletResponse response, HttpServletRequest request) {
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
			
			SSHExec ssh = ExecuteCommand.connect(host, ReadCon.USERNAME, ReadCon.PASSWORD);
			
			CustomTask ct2 = new ExecShellScript("/home/wangyj", "./su_root.sh", "root 123");
			
			CustomTask ct1 = new ExecCommand(" /usr/local/bin/expect -c \" set timeout 10; spawn su - ; expect \"密码*\"; send \"123\\r\"; interact  \" ");
			
			Result res = ssh.exec(ct2);
			// 检查执行结果，如果执行成功打印输出，如果执行失败，打印错误信息
			if (res.isSuccess) {
				System.out.println("Return code: " + res.rc);
				System.out.println("sysout: " + res.sysout);
			} else {
				System.err.println("Return code: " + res.rc);
				System.err.println("error message: " + res.error_msg);
			}
			System.out.println("----------");
			Result res2 = ssh.exec(ct2);
			if (res2.isSuccess) {
				System.out.println("Return code: " + res2.rc);
				System.out.println("sysout: " + res2.sysout);
			} else {
				System.err.println("Return code: " + res2.rc);
				System.err.println("error message: " + res2.error_msg);
			}
			
			json.put("code", "0000");
			json.put("message", "上传成功");
		} catch (Exception e1) {
			json.put("code", "1000");
			json.put("message", "上传失败!");
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
