package observer;

import model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * 客户端通信接口
 * @author livejq
 * @since 2020/4/11
 **/
public interface ChatObserver extends Remote {
    boolean update(User user, String message) throws RemoteException;

    String getEmail() throws RemoteException;

    void updateOnline(List<String> clientList) throws RemoteException;
}
