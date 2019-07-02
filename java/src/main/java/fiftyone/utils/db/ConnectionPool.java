package fiftyone.utils.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import fiftyone.utils.db.ConnectionPool.Column.Type;

public class ConnectionPool implements Serializable{

	private static final long serialVersionUID = -6700024128853962680L;
	
	private static Logger msgLogInfo = Logger.getLogger("msgInfo");
	private static DataSource ds;
	private static ConnectionPool pool;

	/**
	 * Ctor.
	 */
	private ConnectionPool() {
	}

	/**
	 * 初始化连接池
	 */
	static {

		PoolProperties p = new PoolProperties();
		p.setJmxEnabled(true);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(100);
		p.setMaxWait(10000);
		p.setRemoveAbandonedTimeout(60);
		p.setLogAbandoned(true);
		p.setRemoveAbandoned(true);
		// p.setJdbcInterceptors(
		// "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
		// "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

		p.setDriverClassName(PropertiesReader.getProperty("DriverClass"));
		p.setUrl(PropertiesReader.getProperty("Url"));
		p.setUsername(PropertiesReader.getProperty("User"));
		p.setPassword(PropertiesReader.getProperty("Password"));
		// 初始化时获取100条连接
		p.setInitialSize(PropertiesReader.getIntProperty("InitialPoolSize", 30));
		// 每60秒检查所有连接池中的空闲连接
		p.setValidationInterval(PropertiesReader.getIntProperty("IdleConnectionTestPeriod", 60));
		// 最大空闲时间,3600秒内未使用则连接被丢弃。若为0则永不丢弃
		p.setMaxIdle(PropertiesReader.getIntProperty("MaxIdle", 20));
		p.setRollbackOnReturn(PropertiesReader.getBooleanProperty("rollbackOnReturn", true));
		ds = new DataSource();
		ds.setPoolProperties(p);
	}

	/**
	 * 获取连接池实例
	 * 
	 * @return
	 */
	public static final ConnectionPool getInstance() {
		if (pool == null) {
			try {
				pool = new ConnectionPool();
			} catch (Exception e) {
				msgLogInfo.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return pool;
	}

	/**
	 * 重载finalize
	 */
	protected void finalize() throws Throwable {
		// DataSources.destroy(ds);
		super.finalize();
	}

	public static Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			msgLogInfo.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 执行SQL语句，支持增、删、改
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static int execSql(String sql) throws Exception {
		int affectedRows = -1;

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.addBatch();

			affectedRows = stmt.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
			throw new Exception("数据库错误!");
		} finally {
			free(null, stmt, conn);
		}
		return affectedRows;
	}

	/**
	 * 批量执行SQL语句，支持增、删、改
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static boolean execBatch(List<String> sqlList) throws Exception {
		Connection conn = null;
		Statement statemenet = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			statemenet = conn.createStatement();
			for (String sql : sqlList) {
				statemenet.addBatch(sql);

			}
			int[] i = statemenet.executeBatch();
			for (int j : i) {
				if (j == 0) {
					conn.rollback();
					statemenet.close();
					conn.close();
					return false;
				}
			}
			conn.commit();
		} catch (Exception ex) {
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
			throw new Exception("数据库错误!");
		} finally {
			statemenet.close();
			conn.close();
		}
		return true;
	}

	/**
	 * 执行语句
	 * 
	 * @Title: execBatch
	 * @Description: 执行sql PreparedStatement
	 * @author wuyy
	 * @date 2016年5月20日 上午10:25:59
	 *
	 * @param sql
	 * @param arrObj
	 */
	public static boolean execBatch(String sql, List<Object[]> arrObj) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);// 1,首先把Auto commit设置为false,不让它自动提交
			pstm = conn.prepareStatement(sql);
			final int batchSize = 1000;
			int count = 0;
			for (Object[] item : arrObj) {
				for (int i = 0; i < item.length; i++) {
					pstm.setObject(i + 1, item[i]);
				}
				pstm.addBatch();
				if (++count % batchSize == 0) {
					pstm.executeBatch(); // 提交一部分;
				}
			}
			pstm.executeBatch(); // 提交剩下的;
			conn.commit();// 2,进行手动提交（commit）
			conn.setAutoCommit(true);// 3,提交完成后回复现场将Auto commit,还原为true,
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
				if (!conn.isClosed()) {
					conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		} finally {
			free(null, pstm, conn);
		}
	}

	/**
	 * 执行查询SQL语句
	 * 
	 * @param sql
	 * @return
	 */
	public static List<LinkedHashMap<String, Object>> query4List(String sql) {
		return query4List(sql, new Object[] {});
	}

	/**
	 * 
	 * @Title: execSearch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author xuk
	 * @date 2016年8月29日 下午3:53:55
	 *
	 * @param sql
	 * @param obj
	 * @return
	 */
	public static List<LinkedHashMap<String, Object>> query4List(String sql, Object... obj) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		List<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql.toString());
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					stmt.setObject(i + 1, obj[i]);
				}
			}
			result = stmt.executeQuery();
			ResultSetMetaData md = result.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
			while (result.next()) {
				LinkedHashMap<String, Object> rowData = new LinkedHashMap<String, Object>(columnCount);
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), result.getObject(i));
				}
				list.add(rowData);
			}
			return list;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return list;
	}

	/**
	 * 
	 * @Title: queryForPageList
	 * @Description: 分页查询
	 * @author wuyy
	 * @date 2016年7月7日 下午2:52:02
	 *
	 * @param sql
	 * @param obj
	 * @param start
	 * @param limit
	 * @return
	 */
	public static List<LinkedHashMap<String, Object>> query4PageList(String sql, int start, int limit, Object... obj) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet result = null;
		StringBuilder pageSql = new StringBuilder();
		pageSql.append(" select * from ( ");
		pageSql.append(" select p.*,rownum rn from ( " + sql);
		pageSql.append(" )p");
		pageSql.append(" where rownum <=" + (start + limit));
		pageSql.append(" )p where rn >" + start);
		List<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(pageSql.toString().toUpperCase());
			if (obj != null) {
				for (int i = 0; i < obj.length; i++) {
					stmt.setObject(i + 1, obj[i]);
				}
			}

			result = stmt.executeQuery();
			ResultSetMetaData md = result.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
			while (result.next()) {
				LinkedHashMap<String, Object> rowData = new LinkedHashMap<String, Object>(columnCount);
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), result.getObject(i));
				}
				list.add(rowData);
			}
			return list;
		} catch (Exception ex) {
			msgLogInfo.error(sql);
			msgLogInfo.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			free(result, stmt, conn);
		}
		return list;
	}

	public static <T> void insertBatch(List<T> ts, Class<T> clazz) {
		
	}


	public <T> String generateSql(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (fields == null || fields.length == 0) {
			return null;
		}
		Table tableAnnotation = clazz.getAnnotation(Table.class);

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
			String column = columnAnnotation.column();
			Type type = columnAnnotation.type();
		}
	}


	public @interface Column{
		String column() default "";
		Type type();

		enum Type {
			VARCHAR,
			INT,
			DOUBLE,
		}
	}

	public @interface Table {
		String table();
	}


	public static void free(ResultSet rs, Statement statement, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
