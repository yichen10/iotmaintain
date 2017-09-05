package com.ztesoft.iot.maintain.readconfig;

import java.util.Properties;

public class ReadCon {
	
	private static String jndiName;
	
	public static String USERNAME;
	
	public static String PASSWORD;
	
	public static String UPLOADTEMPFILEDIR;
	
	public static String ROOTPASSWORD;
	
	public static String SUROOTFILEDIR;
	
	static{
		Properties ptes = new Properties();
		try {
			ptes.load(ReadCon.class.getResourceAsStream("/config.properties"));
			jndiName = ptes.getProperty("jndiname");
			USERNAME = ptes.getProperty("username");
			PASSWORD = ptes.getProperty("password");
			UPLOADTEMPFILEDIR = ptes.getProperty("uploadTempFileDir");
			ROOTPASSWORD = ptes.getProperty("root_password");
			SUROOTFILEDIR = ptes.getProperty("su_root_file_dir");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getJndiName() {
		return jndiName;
	}
	
	
	/*public static String getUsername() {
		Properties ptes = new Properties();
		try {
			ptes.load(ReadCon.class.getResourceAsStream("/config.properties"));
			USERNAME = ptes.getProperty("username");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return USERNAME;
	}
	
	public static String getPassword() {
		Properties ptes = new Properties();
		try {
			ptes.load(ReadCon.class.getResourceAsStream("/config.properties"));
			PASSWORD = ptes.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PASSWORD;
	}*/
	
	

}
