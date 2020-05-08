package ui.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import ui.view.PaneLayer;

/**
 * 二维码donation(本想公众号扫码登录的)
 * @author livejq
 * @since 2020/4/17
 **/
public class QrCode {

    @FXML
    private AnchorPane qrCodePane;

    @FXML
    private JFXButton btnReturn;

    @FXML
    private Pane qrCodeScan;

    @FXML
    private void initialize() {
        String path = QrCode.class.getResource("../util/images/icons/qrCode-donation.jpg" +
                "").toExternalForm();
        ImageView imageView = new ImageView(path);
        qrCodeScan.getChildren().add(imageView);
    }

    @FXML
    void btnReturnAction(ActionEvent event) {
        PaneLayer.loadPaneById("loginPane", qrCodePane);
    }
}
