package ui.view;

import control.ChatController;
import javafx.stage.Stage;
import observer.ChatObserver;

/**
 * 生成托盘绑定窗口
 * @author livejq
 * @since 2020/4/21
 **/
public class LaunchTray {

    private LaunchTray() {}

    private static class InnerLaunch {
        static MyTray myTray = new MyTray();
    }

    public static void bindStage(Stage stage, ChatController chatController, ChatObserver chatObserver, String email) {
        InnerLaunch.myTray.listen(stage, chatController, chatObserver, email);
    }
}
