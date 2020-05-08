package control;

import model.User;
import observer.ChatObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ChatService;
import service.impl.ChatServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

/**
 * 聊天室控制器实现
 * @author livejq
 * @since 2020/4/07
 **/
public class ChatControllerImpl extends UnicastRemoteObject implements ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatControllerImpl.class.getName());

    private final ChatService service = new ChatServiceImpl();

    public ChatControllerImpl() throws RemoteException {}

    @Override
    public void notifyAllClients(User user, String message) throws IOException {
        logger.info("正在发送消息... {}", message);
        service.notifyAllClients(user, message);
    }

    @Override
    public User get(String email) throws RemoteException, SQLException {
        logger.info("正在获取 {} 用户信息...", email);
        return service.get(email);
    }

    @Override
    public void addChatObserver(ChatObserver chatObserver) throws RemoteException {
        logger.info("正在将 {} 用户加入聊天室在线列表...", chatObserver.getEmail());
        service.addChatObserver(chatObserver);
    }

    @Override
    public void removeChatObserver(ChatObserver chatObserver) throws RemoteException {
        logger.info("{} 正在下线...", chatObserver.getEmail());
        service.removeChatObserver(chatObserver);
    }

    @Override
    public boolean isReserved(String email) throws RemoteException {
        logger.info("正在验证该 {} 用户是否合法...", email);
        return service.isReserved(email);
    }

    @Override
    public boolean checkCredentials(String email, String password) throws RemoteException, SQLException {
        logger.info("正在验证 {} 用户的账户密码...", email);
        return service.checkCredentials(email, password);
    }

    @Override
    public boolean checkEmail(String email) throws RemoteException, SQLException {
        logger.info("正在验证是否存在 {} 用户...", email);
        return service.checkEmail(email);
    }

    @Override
    public boolean updatePassword(String email, String password) throws RemoteException, SQLException {
        logger.info("正在更改 {} 用户的密码...", email);
        return service.updatePassword(email, password);
    }

    @Override
    public void updateClientList() throws IOException {
        logger.info("正在更新在线用户列表...");
        service.updateClientList();
    }

    @Override
    public boolean addNewUser(User user) throws RemoteException, SQLException {
        logger.info("正在注册 {} 用户...", user.getEmail());
        return service.addNewUser(user);
    }
}
