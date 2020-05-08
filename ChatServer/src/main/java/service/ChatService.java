package service;

import model.User;
import observer.ChatObserver;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * 聊天业务接口
 * @author livejq
 * @since 2020/4/07
 **/
public interface ChatService {
    void notifyAllClients(User user, String message) throws IOException;

    User get(String email) throws RemoteException, SQLException;

    void addChatObserver(ChatObserver chatObserver) throws RemoteException;

    void removeChatObserver(ChatObserver chatObserver) throws RemoteException;

    boolean isReserved(String email) throws RemoteException;

    boolean checkCredentials(String email, String password) throws RemoteException, SQLException;

    boolean checkEmail(String email) throws RemoteException, SQLException;

    boolean updatePassword(String email, String password) throws RemoteException, SQLException;

    void updateClientList() throws RemoteException;

    boolean addNewUser(User user) throws RemoteException, SQLException;
}
