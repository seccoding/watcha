package com.ktdsuniversity.watcha.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnector extends ObjectReflector {

	private final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private final String URL = "jdbc:oracle:thin:@localhost:1521:XE";

	private final String username;
	private final String password;

	protected Connection conn;
	protected PreparedStatement pstmt; 
	protected ResultSet rs;
	
	public DBConnector(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public boolean open() {
		try {
			Class.forName(this.DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			this.conn = DriverManager.getConnection(this.URL, this.username, this.password);
			this.conn.setAutoCommit(false);
			return true;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected void closeWithoutConnection() {
		if (this.rs != null) {
			try {
				this.rs.close();
			} catch (SQLException e) {}
		}

		if (this.pstmt != null) {
			try {
				this.pstmt.close();
			} catch (SQLException e) {}
		}
		
		this.rs = null;
		this.pstmt = null;
	}
	
	public void rollback() {
		try {
			this.conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.closeWithoutConnection();
		this.closeConnection();
	}
	
	public void close() {
		try {
			this.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.closeWithoutConnection();
		this.closeConnection();
	}
	
	private void closeConnection() {
		if (this.conn != null) {
			try {
				this.conn.close();
			} catch (SQLException e) {}
		}
		
		this.conn = null;
	}
	
}