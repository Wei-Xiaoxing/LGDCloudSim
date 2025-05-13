package org.lgdcloudsim.experiment;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

/**
 * @author 魏鑫磊
 * @date 2024/10/29 5:09
 */
public class DataCenterResourceUsage {

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

        // HashMap<Double,Integer> timeValueMap=new HashMap<>();
        HashMap<Integer, HashMap<Double,Integer>> datacenterTimeValueMap=new HashMap<>();

        Path folder = Paths.get(DB_DIR);
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        Path file = Paths.get(DB_NAME);
        String dbPath = folder.resolve(file).toString();

        try {
            // 1. 注册 JDBC 驱动（不再需要显式调用 Class.forName() 对于新版本的 JDBC 驱动）
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 打开连接
            connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);

//            connection = DriverManager.getConnectionJDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // 3. 执行查询
            String sql = "SELECT * FROM instance";
            preparedStatement = connection.prepareStatement(sql);

            // 4. 从数据库获取结果集
            resultSet = preparedStatement.executeQuery();

            // 5. 处理结果集
            while (resultSet.next()) {
                int datacenterId=resultSet.getInt("datacenter");
                double startTime=resultSet.getDouble("startTime");
                double finishTime=resultSet.getDouble("finishTime");
                int usage=resultSet.getInt("cpu");
                if (startTime==-1) {
                    continue;
                }
                if (!datacenterTimeValueMap.containsKey(datacenterId)) {
                    datacenterTimeValueMap.put(datacenterId, new HashMap<>());
                }
                HashMap<Double,Integer> timeValueMap=datacenterTimeValueMap.get(datacenterId);
                if (!timeValueMap.containsKey(startTime)) {
                    timeValueMap.put(startTime,0);
                }
                timeValueMap.put(startTime,timeValueMap.get(startTime)+usage);
                if (!timeValueMap.containsKey(finishTime)) {
                    timeValueMap.put(finishTime,0);
                }
                timeValueMap.put(finishTime,timeValueMap.get(finishTime)-usage);
//
//                // 通过字段检索
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//
//                // 输出数据
//                System.out.print("ID: " + id);
//                System.out.println(", Name: " + name);
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

        TreeSet<Integer> datacenterIdSortedKeys = new TreeSet<>(datacenterTimeValueMap.keySet());


//        List<> list=timeValueMap.keySet().stream().sorted().toList();
//        System.out.println(timeValueMap.values().);
        // 创建一个TreeSet来存储键，并自然排序（升序）
        // TreeSet<Double> sortedKeys = new TreeSet<>(timeValueMap.keySet());

        // // 创建一个列表来存储排序后的Map.Entry对象
        // List<Map.Entry<Double, Integer>> sortedEntries = new ArrayList<>();

        // Integer usage=0;

        // 遍历排序后的键，并从HashMap中获取值，添加到列表中
        // for (Double key : sortedKeys) {
        //     usage+=timeValueMap.get(key);
        //     sortedEntries.add(new AbstractMap.SimpleEntry<>(key, usage));
        // }

        // System.out.println(sortedEntries);

    }
}
