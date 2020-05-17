package connector;

import control.ChatController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.Properties;

/**
 * 通过rmi获取远程服务端控制器
 * @author livejq
 * @since 2020/4/07
 **/
public class ServerConnector {

    private ChatController chatController;
    private static final Logger logger = LoggerFactory.getLogger(ServerConnector.class.getName());

    private static class InnerConnector {
        static ServerConnector serverConnector = new ServerConnector();
    }

    private ServerConnector() {
        Properties properties = new Properties();
        String url = "";
        try {
            logger.info("正在读取服务器配置... {}", System.getProperty("user.dir"));
            InputStream input = this.getClass().getResourceAsStream("/settings.properties");
            properties.load(input);
            logger.info(properties.getProperty("server-ip"));
            url = String.format("rmi://%s:%s/ChatServer", properties.getProperty("ip"), properties.getProperty("port"));
            logger.info("正在查找服务器... {}", url);
            chatController = (ChatController) Naming.lookup(url);
        } catch (IOException | NotBoundException e) {
            logger.error("请查看服务器地址是否有误, {}", url, e);
        }
    }

    public static ServerConnector getServerConnector() {
        logger.info("准备获取远程实例...");
        return InnerConnector.serverConnector;
    }

    public ChatController getChatController() {
        return chatController;
    }
}
