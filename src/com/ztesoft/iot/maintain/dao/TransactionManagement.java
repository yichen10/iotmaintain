package com.ztesoft.iot.maintain.dao;

import java.sql.Connection;
import java.util.ArrayList;

public class TransactionManagement {
	private Connection con;
	private ArrayList<Object[]> paras;
	
	public TransactionManagement(ArrayList<Object[]> paras) {
		this.paras = paras;
	}
	
	public TransactionManagement(Connection con, ArrayList<Object[]> paras) {
		this.con = con;
		this.paras = paras;
	}
	
	public void execute(){
		
	}

}
