package observable;

import model.User;
import observer.ChatObserver;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 为聊天用户分配空间
 * @author livejq
 * @since 2020/4/08
 **/
public class ChatObservable {
    //应确保同步，避免线程争抢
    private volatile List<ChatObserver> chatObserverList = new ArrayList<>(3);

    public void addChatObserver(ChatObserver chatObserver) {
        chatObserverList.add(chatObserver);
    }

    public void removeChatObserver(ChatObserver chatObserver) {
        chatObserverList.remove(chatObserver);
    }

    public void notifyAllClients(User user, String message) {
        chatObserverList.stream().filter(Objects::nonNull).forEach(observer -> {
            try {
                observer.update(user, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isReserved(String email) throws RemoteException {
        for (ChatObserver observer : chatObserverList) {
            if (observer.getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public void updateClientList() {
        List<String> usersList = new ArrayList<>();
        chatObserverList.stream().filter(Objects::nonNull).forEach(observer -> {
            try {
                usersList.add(observer.getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        chatObserverList.stream().filter(Objects::nonNull).forEach(observer -> {
            try {
                observer.updateOnline(usersList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
