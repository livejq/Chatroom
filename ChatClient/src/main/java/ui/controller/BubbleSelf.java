package ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;

/**
 * 用户自身消息气泡
 * @author livejq
 * @since 2020/4/22
 **/
public class BubbleSelf {
    @FXML
    private Label bubbleNameLabel;

    @FXML
    private Label bubbleTimeLabel;

    @FXML
    private HBox bubbleFlow;

    @FXML
    private Circle avatar;

    public Label getBubbleNameLabel() {
        return bubbleNameLabel;
    }

    public Label getBubbleTimeLabel() {
        return bubbleTimeLabel;
    }

    public HBox getBubbleFlow() {
        return bubbleFlow;
    }

    public Circle getAvatar() {
        return avatar;
    }

}
