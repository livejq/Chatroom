package ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import connector.ServerConnector;
import control.ChatController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.view.AlertMaker;
import ui.view.PaneLayer;
import ui.view.TransferData;
import util.LittleUtil;
import util.RegexUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录窗体控制器
 * @author livejq
 * @since 2020/4/13
 **/
public class Login {

    private double xPressed;
    private double yPressed;
    private double xStage;
    private double yStage;
    private Stage primaryStage;
    private ChatController chatController;
    private SimpleIntegerProperty sip;
    private static final Logger logger = LoggerFactory.getLogger(Login.class.getName());

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXButton btnLogin;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //设置登录快捷键
        KeyCombination keyCodeCombination = new KeyCodeCombination(KeyCode.ENTER);
        primaryStage.getScene().getAccelerators().put(keyCodeCombination, () -> {
            if (!btnLogin.isDisable()) {
                login(txtEmail.getText(), txtPassword.getText(), chatController);
            }
        });
    }

    @FXML
    private void initialize() {
        chatController = ServerConnector.getServerConnector().getChatController();
        sip = new SimpleIntegerProperty();
        sip.addListener((observable, oldValue, newValue) -> {
            boolean result = (Boolean) txtEmail.getUserData() && (Boolean) txtPassword.getUserData();
            btnLogin.setDisable(!result);
        });

        //先初始化，避免空指针异常
        txtEmail.setUserData(false);
        txtPassword.setUserData(false);
        try {
            txtEmail.textProperty().addListener(((observable, oldValue, newValue) -> {
                if (!LittleUtil.equalsNull(txtEmail.getText()) && RegexUtil.validateEmailAddress(txtEmail.getText()) && txtEmail.getText().length() <= 50) {
                    txtEmail.setUserData(true);
                    sip.set(1);//设置数字时，若与此时存储的数字一致，则默认不调用监听刷新
                } else {
                    txtEmail.setUserData(false);
                    sip.set(2);
                }
            }));
            txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!LittleUtil.equalsNull(txtPassword.getText()) && txtPassword.getText().length() <= 20) {
                    txtPassword.setUserData(true);
                    sip.set(3);
                } else {
                    txtPassword.setUserData(false);
                    sip.set(4);
                }
            });
        } catch (NullPointerException e) {
            logger.error("登录出现异常！", e);
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        //记录当前鼠标按下的坐标
        xPressed = event.getScreenX();
        yPressed = event.getScreenY();
        xStage = primaryStage.getX();
        yStage = primaryStage.getY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        //窗口的位置等于原位置加/减鼠标拖动的距离
        primaryStage.setX(xStage + event.getScreenX() - xPressed);
        primaryStage.setY(yStage + event.getScreenY() - yPressed);
    }

    @FXML
    private void handleWindowClosed(MouseEvent event) {
//        (Stage) topPane.getScene().getWindow().close();
//        Platform.exit();
//        System.exit(0);
        primaryStage.close();
    }

    @FXML
    private void handleWindowMinimized(MouseEvent event) {
        primaryStage.setIconified(true);
    }

    @FXML
    private void createAccount(MouseEvent event) {
        Stage accountSage = new Stage();
        accountSage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CreateAccount.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            CreateAccount createNewAccount = fxmlLoader.getController();
            createNewAccount.setAccountStage(accountSage);
            accountSage.setScene(scene);
            accountSage.getIcons().add(new Image("/util/images/icons/chatroom.png"));
            accountSage.initOwner(primaryStage);
            accountSage.initModality(Modality.WINDOW_MODAL);
            accountSage.showAndWait();
        } catch (IOException e) {
            logger.error("注册窗口创建失败！", e);
        }
    }

    @FXML
    private void handleLogin(MouseEvent event) {
        login(txtEmail.getText(), txtPassword.getText(), chatController);
    }

    @FXML
    private void qrCodeAction(ActionEvent event) {
        PaneLayer.loadPaneByPath("/fxml/QrCode.fxml", stackPane, loginPane);
    }

    @FXML
    private void handleFindPassword(MouseEvent event) {
        PaneLayer.loadPaneByPath("/fxml/FindPassword.fxml", stackPane, loginPane);
    }
    
    private void login(String email, String password, ChatController chatController) {
        try {
            boolean isValid = chatController.checkCredentials(email, password);
            if (isValid) {
                if (!chatController.isReserved(email)) {
                    Map<String, Object> msg = new HashMap<>(1);
                    msg.put("user", chatController.get(email));
                    TransferData.loadPaneByPathWithData("/fxml/LoginProgress.fxml", stackPane, loginPane, msg, PaneLayer.getStorage());
                } else {
                    AlertMaker.showMaterialDialog(stackPane, loginPane, "登录失败", "您已在另一台设备上登录");
                }
            } else {
                //fixme 若点击登录并提示错误后未修改，则直接弹出警告，不用重复访问数据库
                AlertMaker.showMaterialDialog(stackPane, loginPane, "登录失败", "请检查您输入的邮箱或密码是否正确");
            }
        } catch (NullPointerException | IOException | SQLException e) {
            logger.error("登录失败！", e);
        }
    }
}
