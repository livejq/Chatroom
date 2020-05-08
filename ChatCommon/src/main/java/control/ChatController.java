package control;

import model.User;
import observer.ChatObserver;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * 聊天控制接口
 * @author livejq
 * @since 2020/4/07
 **/
public interface ChatController extends Remote {
    void notifyAllClients(User user, String message) throws IOException;

    User get(String email) throws RemoteException, SQLException;

    void addChatObserver(ChatObserver chatObserver) throws RemoteException;

    void removeChatObserver(ChatObserver chatObserver) throws RemoteException;

    boolean isReserved(String email) throws RemoteException;

    boolean checkCredentials(String email, String password) throws RemoteException, SQLException;

    boolean checkEmail(String email) throws RemoteException, SQLException;

    boolean updatePassword(String email, String password) throws RemoteException, SQLException;

    void updateClientList() throws IOException;

    boolean addNewUser(User user) throws RemoteException, SQLException;
}
