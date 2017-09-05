package com.ztesoft.iot.maintain.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ChangeEmailPassword {
	private String username = "";
	private String oldpassword = "";
	private Connection conn = null;
	private boolean hasError = false;
	private String ErrorMessage = "";
	private boolean isSuccessfully = false;
	private String SystemMessage = "";
	public static final String HOST = "127.0.0.1";

	// server ip
	boolean isSuccessfully() {
		return isSuccessfully;
	}

	public boolean isHasError() {
		return hasError;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String msg) {
		hasError = true;
		this.ErrorMessage = msg;
	}

	public ChangeEmailPassword(String username, String oldpassword) {
		this.username = username;
		this.oldpassword = oldpassword;
		try {
			conn = new Connection(HOST);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username, oldpassword);
			if (isAuthenticated == false) {
				setErrorMessage("Authentication failed.");
				conn = null;
			}
		} catch (Exception e) {
			conn.close();
			conn = null;
			System.out.println(e);
		}
	}

	public void setNewPassword(String newpassword) {
		if (hasError) {
			return;
		}
		if (conn == null) {
			return;
		}
		try {
			Session sess = conn.openSession();
			sess.execCommand("passwd");
			InputStream so = sess.getStdout();
			InputStream err = sess.getStderr();
			OutputStream out = sess.getStdin();
			byte[] buffer = new byte[500];
			// 其实没有必要这么大.130就差不多了.怕万一有什么提示.
			int length = 0;
			length = err.read(buffer);
			//
			if (length > 0) {
				//
				System.out.println("#1:" + new String(buffer, 0, length));
				//
				// (current) UNIX password: //
			}
			String coldpassword = oldpassword + "/n";
			out.write(coldpassword.getBytes());
			length = err.read(buffer); //
			if (length > 0) { //
				System.out.println("#2:" + new String(buffer, 0, length));
				// //(current) UNIX password: //
			}
			String cnewpass = newpassword + "/n";
			out.write(cnewpass.getBytes());
			length = err.read(buffer);
			if (length > 0) {
				String rs = new String(buffer, 0, length);
				//
				System.out.println("#3:" + rs);
				if (rs.indexOf("BAD") > -1) {
					sess.close();
					conn.close();
					setErrorMessage(rs);
					return;
				}
			}
			out.write(cnewpass.getBytes());
			length = so.read(buffer);
			if (length > 0) {
				String rs = new String(buffer, 0, length);
				if (rs.indexOf("successfully") > -1) {
					this.isSuccessfully = true;
					this.SystemMessage = rs;
				}
			}
			sess.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public static void main(String[] args) {
		ChangeEmailPassword cep = new ChangeEmailPassword("username", "oldpassword");
		if (cep.isHasError()) {
			System.out.println(cep.getErrorMessage());
			cep = null;
			return;
		}
		cep.setNewPassword("newpassword");
		if (cep.isHasError()) {
			System.out.println(cep.getErrorMessage());
			cep = null;
			return;
		}
		if (cep.isSuccessfully) {
			System.out.println(cep.getSystemMessage());
		}
	}

	public String getSystemMessage() {
		return SystemMessage;
	}
	// }}}}
}
