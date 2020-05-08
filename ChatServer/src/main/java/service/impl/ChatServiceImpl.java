package service.impl;

import dao.UsersDAO;
import dao.impl.UsersDAOImpl;
import model.User;
import observable.ChatObservable;
import observer.ChatObserver;
import service.ChatService;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天业务实现
 * @author livejq
 * @since 2020/4/08
 **/
public class ChatServiceImpl implements ChatService {
    private static final ChatObservable CHAT_OBSERVABLE = new ChatObservable();
    private final UsersDAO usersDAO = new UsersDAOImpl();

    @Override
    public void notifyAllClients(User uesr, String message) {
        CHAT_OBSERVABLE.notifyAllClients(uesr, message);
    }

    @Override
    public User get(String email) throws SQLException {
        return usersDAO.get(email);
    }

    @Override
    public void addChatObserver(ChatObserver chatObserver) {
        CHAT_OBSERVABLE.addChatObserver(chatObserver);
    }

    @Override
    public void removeChatObserver(ChatObserver chatObserver) {
        CHAT_OBSERVABLE.removeChatObserver(chatObserver);
    }

    @Override
    public boolean isReserved(String email) throws RemoteException {
        return CHAT_OBSERVABLE.isReserved(email);
    }

    @Override
    public boolean checkCredentials(String email, String password) throws SQLException {
        User user = usersDAO.get(email);
        try {
            if (user.getEmail().equals(email) && user.getPassword().equals(password))
                return true;
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean checkEmail(String email) throws SQLException {
        return usersDAO.get(email) != null;
    }

    @Override
    public boolean updatePassword(String email, String password) throws SQLException {
        return usersDAO.updatePassword(email, password);
    }

    @Override
    public void updateClientList() {
        CHAT_OBSERVABLE.updateClientList();
    }

    @Override
    public boolean addNewUser(User user) throws SQLException {
        return usersDAO.add(user);
    }
}
