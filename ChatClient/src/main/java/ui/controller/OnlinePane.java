package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

/**
 * 在线用户列表
 * @author livejq
 * @since 2020/4/25
 **/
public class OnlinePane {
    @FXML
    private Label onlineNameLabel;

    @FXML
    private Label onlineTimeLabel;

    @FXML
    private Circle avatar;

    public Label getOnlineNameLabel() {
        return onlineNameLabel;
    }

    public Label getOnlineTimeLabel() {
        return onlineTimeLabel;
    }

    public Circle getAvatar() {
        return avatar;
    }
}
