package fiftyone.utils.db;

import java.sql.*;

/**
 * @author : Stephen
 * @date : 2019/7/3 10:56
 * @description :
 */
public class SQLiteUtils {

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\Po\\ewit\\D1\\code\\d1-core\\D1.db","","");
            PreparedStatement preparedStatement = conn.prepareStatement("insert into user_info(name, age, sex) values ('测试返回id', '100', 'hello')");
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            while (rs.next()) {
                Long id  = rs.getLong(1);
                System.out.println("id: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
