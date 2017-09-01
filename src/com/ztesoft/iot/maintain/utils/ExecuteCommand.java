package com.ztesoft.iot.maintain.utils;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.Result;
import net.neoremind.sshxcute.core.SSHExec;
import net.neoremind.sshxcute.exception.TaskExecFailException;
import net.neoremind.sshxcute.task.CustomTask;
import net.neoremind.sshxcute.task.impl.ExecCommand;
import net.neoremind.sshxcute.task.impl.ExecShellScript;

public class ExecuteCommand {

	public static SSHExec SSH;
	
	public static void main(String[] args) {
		ExecuteCommand ec = new ExecuteCommand();
		ec.test();
	}
	
	//public static SSHExec ssh = null;
	
	public static SSHExec connect(String host, String username, String password) {
		// 新建一个SSHExec引用
		// 我们下面所有的代码都放在try-catch块中
		try {
			// 实例化一个ConnBean对象，参数依次是IP地址、用户名和密码
			ConnBean cb = new ConnBean(host, username, password);
			// 将刚刚实例化的ConnBean对象作为参数传递给SSHExec的单例方法得到一个SSHExec对象
			SSH = SSHExec.getInstance(cb);
			// 连接服务器
			SSH.connect();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		return SSH;
	}
	
	public static void disConnect(SSHExec ssh){
		if (ssh.connect()) {
			ssh.disconnect();
		}
	}
	
	public static void uploadSingleDataToServer(String fromLocalFile, String toServerFile) {
		try {
			//SSH.uploadSingleDataToServer("D:\\sshxcute_test.sh", "/home/wangyj/");
			SSH.uploadSingleDataToServer(fromLocalFile, toServerFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void execShellScript(String workingDir, String shellPath, String args){
		CustomTask ct2 = new ExecShellScript(workingDir,shellPath,args);
		try {
			SSH.exec(ct2);
		} catch (TaskExecFailException e) {
			e.printStackTrace();
		}
	}
	
	public void test() {
		// 新建一个SSHExec引用
		SSHExec ssh = null;
		// 我们下面所有的代码都放在try-catch块中
		try {
			// 实例化一个ConnBean对象，参数依次是IP地址、用户名和密码
			ConnBean cb = new ConnBean("192.168.25.15", "wangyj", "123");
			// 将刚刚实例化的ConnBean对象作为参数传递给SSHExec的单例方法得到一个SSHExec对象
			ssh = SSHExec.getInstance(cb);
			// 连接服务器
			ssh.connect();
			// 上传shell脚本到/home/wangyj目录
			ssh.uploadSingleDataToServer("D:\\sshxcute_test.sh", "/home/wangyj/");
			// 新建一个ExecCommand对象，引用必须是其继承的CustomTask类
			CustomTask ct1 = new ExecCommand("chmod 755 /home/wangyj/sshxcute_test.sh");
			//
			CustomTask ct3 = new ExecCommand("sed -i 's/\r$//' /home/wangyj/sshxcute_test.sh ");
			// 新建一个ExecShellScript对象，引用必须是其继承的CustomTask类
			CustomTask ct2 = new ExecShellScript("/home/wangyj", "./sshxcute_test.sh", "hello world");
			// 执行命令
			ssh.exec(ct1);
			ssh.exec(ct3);
			// 执行脚本并且返回一个Result对象
			Result res = ssh.exec(ct2);
			// 检查执行结果，如果执行成功打印输出，如果执行失败，打印错误信息
			if (res.isSuccess) {
				System.out.println("Return code: " + res.rc);
				System.out.println("sysout: " + res.sysout);
			} else {
				System.err.println("Return code: " + res.rc);
				System.err.println("error message: " + res.error_msg);
			}
		} catch (TaskExecFailException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			ssh.disconnect();
		}

	}

}
