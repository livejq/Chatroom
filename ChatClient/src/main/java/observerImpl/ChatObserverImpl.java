package observerImpl;

import model.User;
import observer.ChatObserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * 实现动态聊天业务
 * @author livejq
 * @since 2020/4/11
 **/
public class ChatObserverImpl extends UnicastRemoteObject implements ChatObserver {

    private final ChatObserver observer;

    public ChatObserverImpl(ChatObserver observer) throws RemoteException {
        this.observer = observer;
    }

    @Override
    public boolean update(User user, String message) throws RemoteException {
        return observer.update(user, message);
    }

    @Override
    public String getEmail() throws RemoteException {
        return observer.getEmail();
    }

    @Override
    public void updateOnline(List<String> clientList) throws RemoteException {
        observer.updateOnline(clientList);
    }
}
