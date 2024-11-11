package org.lgdcloudsim.experiment;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author 魏鑫磊
 * @date 2024/10/29 5:09
 */
public class RequestSccuess {

    // JDBC URL, 用户名和密码
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "password";

    private static final String DB_DIR = "./RecordDb";

    private static final String DB_NAME = "LGDCloudSim.db";

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        HashMap<Double,Integer> timeValueMap=new HashMap<>();

        Path folder = Paths.get(DB_DIR);
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        Path file = Paths.get(DB_NAME);
        String dbPath = folder.resolve(file).toString();

        int count=0;
        double value=0;

        try {
            // 1. 注册 JDBC 驱动（不再需要显式调用 Class.forName() 对于新版本的 JDBC 驱动）
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 打开连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);

//            connection = DriverManager.getConnectionJDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // 3. 执行查询
            String sql = "SELECT\r\n" + //
                                "    submitTime,\r\n" + //
                                "    SUM(CASE WHEN (state IS NULL or state='SUCCESS') THEN 1 ELSE 0 END) AS successNum, COUNT(*) AS sumNum, CAST(SUM(CASE WHEN (state IS NULL or state='SUCCESS') THEN 1 ELSE 0 END) AS REAL) / COUNT(*) * 100.0 AS successRate\r\n" + //
                                "FROM\r\n" + //
                                "    userRequest\r\n" + //
                                "GROUP BY\r\n" + //
                                "    submitTime;";
            preparedStatement = connection.prepareStatement(sql);

            // 4. 从数据库获取结果集
            resultSet = preparedStatement.executeQuery();

            // 5. 处理结果集
            while (resultSet.next()) {
                count++;
                value+=resultSet.getDouble("successRate");
                // System.err.println(resultSet.getDouble("successRate"));
            }
        } catch (SQLException e) {
            // 处理 JDBC 错误
            e.printStackTrace();
        } finally {
            // 6. 清理环境
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (connection != null) connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        
        System.out.println(value/count);
    }
}
