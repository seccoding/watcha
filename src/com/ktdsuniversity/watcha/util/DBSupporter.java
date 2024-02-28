package com.ktdsuniversity.watcha.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBSupporter extends DBConnector {

	public DBSupporter(String username, String password) {
		super(username, password);
	}

	public int insert(String query, Object[] params) {
		if (super.conn == null) {
			throw new RuntimeException("DB연결이 종료되었습니다.");
		}
		try {
			super.pstmt = super.conn.prepareStatement(query);
			super.setParams(params, pstmt);
			return super.pstmt.executeUpdate();
		} catch (SQLException | RuntimeException e) {
			rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeWithoutConnection();
		}
	}

	public int update(String query, Object[] params) {
		return this.insert(query, params);
	}

	public int delete(String query, Object[] params) {
		return this.insert(query, params);
	}

	public <T> List<T> selectList(String query, Object[] params, Class<T> type) {
		if (super.conn == null) {
			throw new RuntimeException("DB연결이 종료되었습니다.");
		}
		
		try {
			super.pstmt = super.conn.prepareStatement(query);
			super.setParams(params, super.pstmt);

			super.rs = super.pstmt.executeQuery();
			
			List<T> result = new ArrayList<>();
			while (super.rs.next()) {
				T t = createNewInstance(type);
				if (t != null) {
					invokeSetter(t, super.rs);
					result.add(t);
				}
			}

			return result;
		} catch (SQLException | RuntimeException e) {
			rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeWithoutConnection();
		}
	}

	public <T> T selectOne(String query, Object[] params, Class<T> type) {
		long count = getCount(query, params);
		if (count > 1) {
			super.rollback();
			throw new RuntimeException("1개 이상의 행이 리턴되었습니다.");
		}
		
		if (super.conn == null) {
			throw new RuntimeException("DB연결이 종료되었습니다.");
		}
		
		try {
			super.pstmt = super.conn.prepareStatement(query);
			super.setParams(params, super.pstmt);
			super.rs = super.pstmt.executeQuery();

			T t = null;
			if (super.rs.next()) {
				
				t = createNewInstance(type);
				if (t != null) {
					invokeSetter(t, super.rs);
				}
			}

			return t;
		} catch (SQLException | RuntimeException e) {
			rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeWithoutConnection();
		}
	}
	
	private long getCount(String query, Object[] params) {
		if (super.conn == null) {
			throw new RuntimeException("DB연결이 종료되었습니다.");
		}
		
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT COUNT(1) FROM ( ");
			sb.append(query);
			sb.append(" ) ");
			
			super.pstmt = super.conn.prepareStatement(sb.toString());
			super.setParams(params, super.pstmt);

			super.rs = super.pstmt.executeQuery();

			if (super.rs.next()) {
				return rs.getInt(1);
			}
			
			return 0;
		} catch (SQLException | RuntimeException e) {
			rollback();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeWithoutConnection();
		}
	}
	
}