package fiftyone.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: MySQLConnectionUtils
 * @Description: 未采用连接池的数据源
 * @Author zhangxw
 * @Date 2017年8月2日 上午11:03:50
 */
public class MySQLConnectionUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MySQLConnectionUtils.class);
	private static final Integer SAVE_MAX_SIZE = 200;
	
	// 加载驱动
	static {
		
		try {
//			DriverManager.registerDriver(new Driver());
			String driverClass = PropertiesReader.getProperty("driverClass");
			Class.forName(driverClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface ResultSetHandler<T> {
		public T handle(ResultSet rs);
	}
	
	public static Connection getConnection() {
		String url = PropertiesReader.getProperty("url");
		String username = PropertiesReader.getProperty("username");
		String password = PropertiesReader.getProperty("password");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return conn;
	}
	
	
	public static void saveOrUpdateBatch(String sql, Object[]... params) {
		
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			statement = conn.prepareStatement(sql);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					Object[] param = params[i];
					// 写入参数
					for (int j = 0; j < param.length; j++) {
						statement.setObject(j, param[j]);
					}
					// 分页写入
					if ( i % SAVE_MAX_SIZE == 0) {
						statement.executeBatch();
						statement.clearBatch();
					}
					statement.addBatch();
				}
			}
			// 写入最后一个
			statement.executeBatch();
			conn.commit();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				LOGGER.error(e1.getMessage(), e1);
			}
		} finally {
			close(conn, statement, null);
		}
	}

	public static <T> T executeQuery(String sql, ResultSetHandler<T> handler, Object... params) {
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		T t = null;
		try {
			conn = getConnection();
			preStatement = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					preStatement.setObject(i+1, params[i]);
				}
			}
			rs = preStatement.executeQuery();
			t = handler.handle(rs);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			close(conn, preStatement, rs);
		}
		return t;
	}
	
	
	public static void close(Connection conn, Statement statement, ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		if (statement != null) {
			try {
				statement.close();
			} catch(Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
