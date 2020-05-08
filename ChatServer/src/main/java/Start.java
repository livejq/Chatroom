import control.ChatControllerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

/**
 * 聊天室服务器启动器
 * @author livejq
 * @since 2020/4/11
 **/
public class Start {

    private static final Logger logger = LoggerFactory.getLogger(Start.class.getName());

    public static void main(String[] args) {

        Properties properties = new Properties();
        StringBuilder sb = new StringBuilder();
        try {
            logger.info("正在读取服务器配置...");
            InputStream input = new FileInputStream(new File("ChatServer\\config\\settings.properties"));
            properties.load(input);

            String ip = properties.getProperty("ip");
            String registryPort = properties.getProperty("registryPort");
            sb.append("ip:" + ip + ", port:" + registryPort);
            System.setProperty("java.rmi.server.hostname", ip);

            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(registryPort));
            registry.rebind("ChatServer", new ChatControllerImpl());

            logger.info("正在启动服务...");
            logger.info(sb.toString());

            //开启PowerShell窗口
            /*String[] ar = {"cmd", "/c", "start", "powershell.exe", "-command", "Read-Host", " 服务启动成功，", "请按 Enter 退出..."};
            Process process = Runtime.getRuntime().exec(ar);
            InputStream stderr = process.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stderr));
            reader.readLine();
            reader.close();
            process.destroy();
            logger.info("正在退出...");
            Runtime.getRuntime().exit(0);*/
        } catch (IOException e) {
            logger.error("请查看服务器配置是否有误，{}", sb.toString(), e);
        }

    }
}
