package ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import connector.ServerConnector;
import control.ChatController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.User;
import observer.ChatObserver;
import observerImpl.ChatObserverImpl;
import ui.view.LoaderMaker;
import ui.view.PaneLayer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;


/**
 * 登录进度动画
 * @author livejq
 * @since 2020/4/14
 **/
public class LoginProgress {

    private ChatController chatController;
    private User user;
    private Task<Void> progressTask;

    @FXML
    private AnchorPane loginProgressPane;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Text txtProgress;

    @FXML
    private JFXButton btnCancel;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void initialize() {
        progressTask = processAnimation();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(progressTask.progressProperty());
        txtProgress.textProperty().bind(progressTask.messageProperty());
        progressTask.setOnSucceeded(event -> {
            Stage chatroom = new Stage();
            FXMLLoader fxmlLoader = LoaderMaker.createLoader("../fxml/Chatroom.fxml");
            try {
                chatroom.setScene(new Scene(fxmlLoader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Chatroom chatroomController = fxmlLoader.getController();
            chatroomController.setPrimaryStage(chatroom);
            chatroomController.getSideEmailLabel().setText(user.getEmail());
            chatroomController.getSideNameLabel().setText(user.getNickname());
            chatroomController.setUser(user);

            try {
                //将此登录用户加入到聊天队列中
                ChatObserver chatObserver = new ChatObserverImpl(chatroomController);
                chatController = ServerConnector.getServerConnector().getChatController();
                System.out.println("正在加入在线用户列表...");
                chatController.addChatObserver(chatObserver);
                chatroomController.setChatObserver(chatObserver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            chatroomController.initAfter();

            try {
                String path = new File(user.getAvatar() + user.getEmail() + ".png").toURI().toString();
                chatroomController.getAvatar().setFill(new ImagePattern(new Image(path)));
            } catch (Exception e) {
                System.out.println("没有找到相应的用户头像!");
                e.printStackTrace();
            }

            chatroom.initStyle(StageStyle.TRANSPARENT);
            chatroom.getIcons().add(new Image("/ui/util/images/icons/chatroom.png"));
            ((Stage) btnCancel.getScene().getWindow()).close();
            System.out.println("初始化完毕!");
            chatroom.show();
        });

        new Thread(progressTask).start();
    }

    @FXML
    void cancelAction(ActionEvent event) {
        progressTask.cancel();
        PaneLayer.loadPaneById("loginPane", loginProgressPane);
    }

    /**
     * 创建进度条加载动画
     *
     * @return javafx.concurrent.Task<java.lang.Void>
     */
    private Task<Void> processAnimation() {
        return new Task<Void>() {

            @Override
            protected void succeeded() {
                updateMessage("加载完毕!");
                super.succeeded();
            }

            @Override
            protected void cancelled() {
                updateMessage("取消成功!");
                super.cancelled();
            }

            @Override
            protected void failed() {
                updateMessage("加载失败!");
                super.failed();
            }

            @Override
            protected Void call() throws Exception {
                System.out.println("聊天室正在初始化中...");
                for (int i = 0; i < 100; i++) {
                    updateProgress(i + 1, 100);
                    updateMessage("加载中..." + " " + (i + 1) + "%");
                    TimeUnit.MILLISECONDS.sleep(20);
                }
                System.out.println("进度条线程：" + Thread.currentThread().getName());

                return null;
            }
        };
    }
}
