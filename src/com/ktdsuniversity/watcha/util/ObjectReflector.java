package com.ktdsuniversity.watcha.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ObjectReflector {

	private interface SetParams {
		public void set(int index, PreparedStatement pstmt, String param) throws SQLException;
	}
	
	private interface GetValue {
		public Object get(ResultSet rs, String columnName) throws SQLException;
	}
	
	private static final Map<Class<?>, SetParams> SET_TYPES;
	private static final Map<Class<?>, GetValue> GET_TYPES;
	
	static {
		SET_TYPES = new HashMap<>();
		SET_TYPES.put(byte.class, (i, pstmt, param) -> pstmt.setByte(i, Byte.parseByte(param)));
		SET_TYPES.put(short.class, (i, pstmt, param) -> pstmt.setShort(i, Short.parseShort(param)));
		SET_TYPES.put(int.class, (i, pstmt, param) -> pstmt.setInt(i, Integer.parseInt(param)));
		SET_TYPES.put(long.class, (i, pstmt, param) -> pstmt.setLong(i, Long.parseLong(param)));
		SET_TYPES.put(float.class, (i, pstmt, param) -> pstmt.setFloat(i, Float.parseFloat(param)));
		SET_TYPES.put(double.class, (i, pstmt, param) -> pstmt.setDouble(i, Double.parseDouble(param)));
		SET_TYPES.put(boolean.class, (i, pstmt, param) -> pstmt.setBoolean(i, Boolean.parseBoolean(param)));
		SET_TYPES.put(String.class, (i, pstmt, param) -> pstmt.setString(i, param));
		
		GET_TYPES = new HashMap<>();
		GET_TYPES.put(byte.class, (rs, columnName) -> rs.getByte(columnName));
		GET_TYPES.put(short.class, (rs, columnName) -> rs.getShort(columnName));
		GET_TYPES.put(int.class, (rs, columnName) -> rs.getInt(columnName));
		GET_TYPES.put(long.class, (rs, columnName) -> rs.getLong(columnName));
		GET_TYPES.put(float.class, (rs, columnName) -> rs.getFloat(columnName));
		GET_TYPES.put(double.class, (rs, columnName) -> rs.getDouble(columnName));
		GET_TYPES.put(boolean.class, (rs, columnName) -> rs.getBoolean(columnName));
		GET_TYPES.put(String.class, (rs, columnName) -> rs.getString(columnName));
	}
	
	protected <T> T createNewInstance(Class<T> type) {
		Constructor<T> constructor;
		try {
			constructor = type.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void setParams(Object[] params, PreparedStatement pstmt) throws SQLException {
		if (params != null && params.length > 0) {
			Object param = null;
			for (int i = 0; i < params.length; i++) {
				param = params[i];
				if (SET_TYPES.containsKey(param.getClass())) {
					SET_TYPES.get(param.getClass()).set(i+1, pstmt, param.toString());
				}
				else {
					System.err.println(param.getClass() + " is not support.");
				}
			}
		}
	}

	protected <T> void invokeSetter(T t, ResultSet rs) throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		
		String columnName = null;
		String fieldName = null;
		Field field = null;
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			columnName = metaData.getColumnName(i);
			fieldName = this.convertToFieldName(columnName);
			field = this.findField(t, fieldName);
			if (field != null) {
				Class<?> fieldType = field.getType();
				if (GET_TYPES.containsKey(fieldType)) {
					Object result = GET_TYPES.get(fieldType).get(rs, columnName);
					this.invokeSetter(t, field, result);
				}
				else {
					System.err.println(fieldType + " is not support.");
				}
			}
		}
	}
	
	private String convertToFieldName(String columnName) {
		columnName = columnName.toLowerCase();

		for (int i = 0; i < columnName.length(); i++) {
			if (columnName.charAt(i) == '_') {
				columnName = columnName.replace("_" + columnName.charAt(i + 1),
						("_" + columnName.charAt(i + 1)).toUpperCase());
			}
		}
		
		return columnName.replace("_", "");
	}
	
	private Field findField(Object object, String variablename) {
		Class<?> cls = object.getClass();
		Field field = null;
		try {
			field = cls.getDeclaredField(variablename);
		} catch (NoSuchFieldException | SecurityException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return field;
	}
	
	private void invokeSetter(Object object, Field field, Object value) {
		if (field != null) {
			String methodName = this.makeMethodName(field.getName(), "set");
			Method setter = this.findMethod(object.getClass(), methodName, field);
			
			if (setter != null) {
				try {
					setter.invoke(object, value);
				} catch (IllegalAccessException | InvocationTargetException e) {
					System.out.println(e.getMessage());
					return;
				}
			}
		}
	}
	
	private String makeMethodName(String memberVariableName, String prefix) {
		String firstLetter = (memberVariableName.charAt(0) + "").toUpperCase();
		String capitalVariableName = firstLetter + memberVariableName.substring(1);
		return prefix + capitalVariableName;
	}

	private Method findMethod(Class<?> cls, String methodName, Field field) {
		Method method = null;
		try {
			if (methodName.startsWith("get")) {
				method = cls.getDeclaredMethod(methodName);
			}
			else if (methodName.startsWith("set")) {
				method = cls.getDeclaredMethod(methodName, field.getType());
			}
		} catch (NoSuchMethodException | SecurityException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return method;
	}
	
}