package ui.view;

import control.ChatController;
import javafx.application.Platform;
import javafx.stage.Stage;
import observer.ChatObserver;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.IOException;


/**
 * 定义托盘
 * @author livejq
 * @since 2020/4/21
 **/
public class MyTray {

    private SystemTray tray;
    private TrayIcon trayIcon;
    private PopupMenu popup;
    private MenuItem showItem;
    private MenuItem exitItem;
    private boolean supported;
    private ActionListener showListener;
    private ActionListener exitListener;
    private MouseListener mouseListener;

    //对象构造块，确保只执行一次
    {
        //若出现乱码问题，试试在VM Options中添加-Dfile.encoding=utf8
        Platform.setImplicitExit(false);
        if (!SystemTray.isSupported()) {
            //不支持系统托盘
            supported = false;
        }
        //不能选择icon格式的图片，要使用16*16的png格式的图片
        Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("../util/images/icons/chatroom-tray.png"));
        tray = SystemTray.getSystemTray();

        showItem = new MenuItem("Open");
        exitItem = new MenuItem("Exit");
        popup = new PopupMenu();

        //添加菜单组件
        popup.add(showItem);
        popup.add(exitItem);

        //创建托盘图标
        trayIcon = new TrayIcon(image, "聊吧", popup);

        //设置图标尺寸自动适应
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        supported = true;
    }

    public MyTray() {}

    public MyTray(Stage stage, ChatController chatController, ChatObserver chatObserver, String email) {
        if (supported) {
            listen(stage, chatController, chatObserver, email);
        }
    }

    /**
     * @param stage 被托盘监听的Stage
     * @return void
     * @Author livejq
     * @Date 2020/4/21
     * @Description 设置系统托盘所监听的Stage
     */
    public void listen(Stage stage, ChatController chatController, ChatObserver chatObserver, String email) {
        //移除原来的事件
        if (showItem != null && showItem.getActionListeners().length > 0) {
            showItem.removeActionListener(showListener);
        }
        if (trayIcon != null && trayIcon.getMouseListeners().length > 0) {
            trayIcon.removeMouseListener(mouseListener);
        }
        if (exitItem != null && exitItem.getActionListeners().length > 0) {
            exitItem.removeActionListener(exitListener);
        }

        //点击"打开"按钮显示窗口
        showListener = e -> Platform.runLater(() -> showStage(stage));

        //鼠标单击显示/隐藏stage
        mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //鼠标左键
                if (e.getButton() == MouseEvent.BUTTON1) {
                    change(stage);
                }
            }
        };

        //点击"退出"按钮关闭程序
        exitListener = e -> {
            try {
                chatController.removeChatObserver(chatObserver);
                System.out.println(email + " 用户退出了聊天，正在更新聊天用户列表...");
                chatController.updateClientList();
                Runtime.getRuntime().exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
                Runtime.getRuntime().exit(1);
            }
        };

        //给菜单项和托盘图标绑定事件
        showItem.addActionListener(showListener);
        exitItem.addActionListener(exitListener);
        trayIcon.addMouseListener(mouseListener);
    }

    /**
     * @param stage 被托盘监听的Stage
     * @return void
     * @Author livejq
     * @Date 2020/4/21
     * @Description 关闭窗口
     */
    private void hide(Stage stage) {
        Platform.runLater(() -> {
            if (stage.isIconified()) {
                stage.setIconified(false);
            }
            //如果支持系统托盘，就隐藏到托盘，不支持就直接退出
            if (supported) {
                //stage.hide()与stage.close()等价
                stage.hide();
            } else {
                System.exit(1);
            }
        });
    }

    /**
     * @param stage 被托盘监听的Stage
     * @return void
     * @Author livejq
     * @Date 2020/4/21
     * @Description 点击系统托盘，显示界面(并且显示在最前面，将最小化的状态设为false)
     */
    private void showStage(Stage stage) {
        //点击系统托盘，
        Platform.runLater(() -> {
            if (stage.isIconified()) {
                stage.setIconified(false);
            }
            if (!stage.isShowing()) {
                stage.show();
            }
            stage.toFront();
        });
    }

    /**
     * @param stage
     * @return void
     * @Author livejq
     * @Date 2020/4/21
     * @Description 鼠标点击托盘展示或隐藏窗体
     */
    private void change(Stage stage) {
        if (stage.isShowing()) {
            hide(stage);
        } else {
            showStage(stage);
        }
    }
}
