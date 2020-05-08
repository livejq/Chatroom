package dao.dbConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接服务
 * @author livejq
 * @since 2020/4/08
 **/
public class DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class.getName());

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Properties config = new Properties();
//        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("settings.properties");
        StringBuilder sb = new StringBuilder();
        try {
            logger.info("正在读取MySQL配置...");
            InputStream input = new FileInputStream(new File("ChatServer\\config\\settings.properties"));
            config.load(input);
            System.out.println("正在连接数据库...");
            String jdbc = config.getProperty("jdbc");
            String username = config.getProperty("username");
            String password = config.getProperty("password");
            sb.append("jdbc=" +jdbc + ", username=" + username + ", password=" + password);
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(jdbc, username, password);
        } catch (IOException e) {
            logger.error("数据库连接失败，请检查地址是否有误，用户名和密码是否正确。{}", sb.toString(), e);
        }
        return null;
    }
}
