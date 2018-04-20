package dbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class C3p0Utils {
	static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(C3p0Utils.class.getName());

	// 通过标识名来创建相应连接池
	static ComboPooledDataSource dataSource = new ComboPooledDataSource("oraclesql");

	// 三剑客
	static Connection con = null;// 连接对象
	static PreparedStatement pstmt = null;// 语句对象
	static ResultSet rs = null;// 结果集对象

	// 从连接池中取用一个连接
	public static Connection getConnection() throws Exception {
		try {
			return dataSource.getConnection();

		} catch (Exception e) {
			logger.error("Exception in C3p0Utils!", e);
			throw new Exception("数据库连接出错!", e);
		}
	}

	
	public static void main(String[] args) throws Exception {
		Connection conn = C3p0Utils.getConnection();
		System.out.println(conn);
	}

	// 释放连接回连接池
	public static void close(Connection conn, PreparedStatement pst, ResultSet rs) throws Exception {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("Exception in C3p0Utils!", e);
				throw new Exception("数据库连接关闭出错!", e);
			}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				logger.error("Exception in C3p0Utils!", e);
				throw new Exception("数据库连接关闭出错!", e);
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error("Exception in C3p0Utils!", e);
				throw new Exception("数据库连接关闭出错!", e);
			}
		}
	}

	/**
	 * 执行更新
	 *
	 * @param sql
	 *            传入的预设的 sql语句
	 * @param params
	 *            问号参数列表
	 * @return 影响行数
	 * @throws Exception
	 */
	public static int execUpdate(String sql, Object[] params) throws Exception {
		try {
//			NewProxyConnection
			con = getConnection();// 获得连接对象
			pstmt = con.prepareStatement(sql);// 获得预设语句对象

			if (params != null) {
				// 设置参数列表
				for (int i = 0; i < params.length; i++) {
					// 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
					pstmt.setObject(i + 1, params[i] + "");
				}
			}

			return pstmt.executeUpdate(); // 执行更新，并返回影响行数

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return 0;
	}

	/**
	 * 执行查询
	 *
	 * @param sql
	 *            传入的预设的 sql语句
	 * @param params
	 *            问号参数列表
	 * @return 查询后的结果
	 * @throws Exception
	 */
	public static List<Map<String, Object>> execQuery(String sql, Object[] params) throws Exception {

		try {
			getConnection();// 获得连接对象
			pstmt = con.prepareStatement(sql);// 获得预设语句对象

			if (params != null) {
				// 设置参数列表
				for (int i = 0; i < params.length; i++) {
					// 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
					pstmt.setObject(i + 1, params[i] + "");
				}
			}

			// 执行查询
			ResultSet rs = pstmt.executeQuery();

			List<Map<String, Object>> al = new ArrayList<Map<String, Object>>();

			// 获得结果集元数据（元数据就是描述数据的数据，比如把表的列类型列名等作为数据）
			ResultSetMetaData rsmd = rs.getMetaData();

			// 获得列的总数
			int columnCount = rsmd.getColumnCount();

			// 遍历结果集
			while (rs.next()) {
				Map<String, Object> hm = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					// 根据列索引取得每一列的列名,索引从1开始
					String columnName = rsmd.getColumnName(i + 1);
					// 根据列名获得列值
					Object columnValue = rs.getObject(columnName);
					// 将列名作为key，列值作为值，放入 hm中，每个 hm相当于一条记录
					hm.put(columnName, columnValue);
				}
				// 将每个 hm添加到al中, al相当于是整个表，每个 hm是里面的一条记录
				al.add(hm);
			}

			return al;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(con, pstmt, rs);
		}
		return null;
	}
}
