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
import net.neoremind.sshxcute.task.impl.ExecShellScript;
import net.sf.json.JSONObject;

public class ExecuteScriptCommand implements ServletInter {

	private HttpServletResponse response;
	private HttpServletRequest request;

	public ExecuteScriptCommand(HttpServletResponse response, HttpServletRequest request) {
		this.response = response;
		this.request = request;
	}

	@Override
	public void doService() {
		JSONObject json = new JSONObject();
		try {
			String host = request.getParameter("host");
			String fileDirectory = request.getParameter("script_dir");
			String scriptCommand = request.getParameter("script_command");
			String isRoot = request.getParameter("is_root");

			SSHExec ssh = ExecuteCommand.connect(host, ReadCon.USERNAME, ReadCon.PASSWORD);

			if (StringUtils.isNotEmpty(isRoot) && "1".equals(isRoot)) {
				String suRootFile = ReadCon.SUROOTFILEDIR + "su_root.sh";
				String userdir = this.getClass().getClassLoader().getResource("").getPath();
				System.out.println(userdir + "su_root.sh");
				// 将文件上传到该系统：
				ssh.uploadSingleDataToServer(userdir + "su_root.sh", suRootFile);
				CustomTask ct1 = new ExecCommand("sed -i 's/\r$//' " + suRootFile);
				CustomTask ct2 = new ExecCommand("chmod 755 " + suRootFile);
				CustomTask ct3 = new ExecShellScript(ReadCon.SUROOTFILEDIR, "./su_root.sh",
						"root " + ReadCon.ROOTPASSWORD + " " + fileDirectory + "  " + scriptCommand);
				Result res1 = ssh.exec(ct1);
				Result res2 = ssh.exec(ct2);
				Result res3 = ssh.exec(ct3);
				if (res1.isSuccess && res2.isSuccess && res3.isSuccess) {
					json.put("code", "0000");
					json.put("message", "脚本执行成功");
				} else {
					json.put("code", "1000");
					json.put("message", "脚本执行异常");
				}
			} else {
				if (StringUtils.isNotEmpty(fileDirectory)) {
					CustomTask ct1 = new ExecShellScript(fileDirectory, scriptCommand, "");
					Result res1 = ssh.exec(ct1);ssh.exec(ct1);
					if (res1.isSuccess) {
						json.put("code", "0000");
						json.put("message", "脚本执行成功");
					} else {
						json.put("code", "1000");
						json.put("message", "脚本执行异常");
					}
				}
			}
			ExecuteCommand.disConnect(ssh);
		} catch (Exception e1) {
			e1.printStackTrace();
			json.put("code", "1000");
			json.put("message", "脚本执行异常");
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
