package ui.controller;

import com.jfoenix.controls.*;
import connector.ServerConnector;
import control.ChatController;
import enums.GenderEnum;
import enums.StrengthEnum;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;
import ui.view.AlertMaker;
import util.CheckStrength;
import util.LittleUtil;
import util.RegexUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * 注册控制器
 * @author livejq
 * @since 2020/4/16
 **/
public class CreateAccount {

    private double xPressed;
    private double yPressed;
    private double xStage;
    private double yStage;
    private ChatController chatController;
    private Stage accountStage;
    private ToggleGroup genderGroup;
    private File file;
    public static final String FAVICON = "ChatClient/users/";

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane newAccountPane;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXPasswordField txtPassword2;

    @FXML
    private Text level;

    @FXML
    private JFXTextField txtNickname;

    @FXML
    private JFXRadioButton male;

    @FXML
    private JFXRadioButton female;

    @FXML
    private JFXRadioButton secret;

    @FXML
    private JFXDatePicker dtPkr;

    @FXML
    private JFXTextField txtPath;

    public void setAccountStage(Stage accountStage) {
        this.accountStage = accountStage;
    }

    @FXML
    private void initialize() {
        chatController = ServerConnector.getServerConnector().getChatController();
        //初始化单选按钮
        genderGroup = new ToggleGroup();
        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);
        secret.setToggleGroup(genderGroup);
        male.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../util/images/icons/male.png"))));
        female.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../util/images/icons/female.png"))));
        secret.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../util/images/icons/secret.png"))));

        //监听密码强度并适当给予用户反馈
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            StrengthEnum pwdLevel = CheckStrength.getPasswordLevel(newValue);
            switch (pwdLevel) {
                case EASY:
                    level.setText(StrengthEnum.EASY.getDescription());
                    level.getStyleClass().clear();
                    level.getStyleClass().add("password-low");
                    break;
                case MIDDLE:
                    level.setText(StrengthEnum.MIDDLE.getDescription());
                    level.getStyleClass().clear();
                    level.getStyleClass().add("password-middle");
                    break;
                case STRONG:
                    level.setText(StrengthEnum.STRONG.getDescription());
                    level.getStyleClass().clear();
                    level.getStyleClass().add("password-strong");
                    break;
                case VERY_STRONG:
                    level.setText(StrengthEnum.VERY_STRONG.getDescription());
                    break;
                default:
                    level.setText(StrengthEnum.EXTREMELY_STRONG.getDescription());
                    break;
            }
        });

        //监听密码是否一致并适当给予用户反馈
        int startSize = txtPassword2.getStyleClass().size();
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txtPassword2.getText().equals(newValue) && txtPassword2.getStyleClass().size() <= startSize) {
                txtPassword2.getStyleClass().add("wrong-credentials");
            }
            if (txtPassword2.getText().equals(newValue) && txtPassword2.getStyleClass().size() >= startSize + 1) {
                txtPassword2.getStyleClass().remove("wrong-credentials");
            }

        });
        txtPassword2.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txtPassword.getText().equals(newValue) && txtPassword2.getStyleClass().size() <= startSize) {
                txtPassword2.getStyleClass().add("wrong-credentials");
            }
            if (txtPassword.getText().equals(newValue) && txtPassword2.getStyleClass().size() >= startSize + 1) {
                txtPassword2.getStyleClass().remove("wrong-credentials");
            }

        });
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        //记录当前鼠标按下的坐标
        xPressed = event.getScreenX();
        yPressed = event.getScreenY();
        xStage = accountStage.getX();
        yStage = accountStage.getY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        //窗口的位置等于原位置加/减鼠标拖动的距离
        accountStage.setX(xStage + event.getScreenX() - xPressed);
        accountStage.setY(yStage + event.getScreenY() - yPressed);
    }

    @FXML
    private void handleWindowClosed(MouseEvent event) {
        accountStage.close();
    }

    @FXML
    private void browseAction(ActionEvent actionEvent) {
        file = showOpenFile();
        if (file != null) {
            txtPath.setText(file.getAbsolutePath());
        }
    }

    private File showOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG (*.jpg)", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));

        return fileChooser.showOpenDialog(txtEmail.getScene().getWindow());
    }

    @FXML
    private void createAction(ActionEvent event) {

        String email, password, password2, nickname, gender, birth, avatar;

        JFXRadioButton selectedRadio = (JFXRadioButton) genderGroup.getSelectedToggle();
        gender = GenderEnum.valueOf(GenderEnum.class, selectedRadio.getId().toUpperCase()).getDescription();


        //用户输入校验
        try {
            //TODO 假的邮箱无法使用找回密码功能；最好的做法是点击注册后向用户邮箱发送确认注册链接，但需要搭建后台Web服务，暂时不搞
            email = txtEmail.getText();
            //TODO 密码最好用非明文的方式加密存储
            password = txtPassword.getText();
            password2 = txtPassword2.getText();
            nickname = txtNickname.getText();
            birth = dtPkr.getValue().toString();
            avatar = txtPath.getText();
            if (LittleUtil.equalsNull(email) || !RegexUtil.validateEmailAddress(email) || email.length() > 50) {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "请填写正确的邮箱地址！");
                txtEmail.requestFocus();
                return;
            }
            if (LittleUtil.equalsNull(password) || !password.equals(password2) || password.length() > 20) {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "请正确填写好密码！");
                txtPassword.requestFocus();
                return;
            }
            if (LittleUtil.equalsNull(nickname) || nickname.length() > 8 || LittleUtil.isSpecialChar(nickname)) {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "昵称最长8位且没有特殊字符！");
                txtNickname.requestFocus();
                return;
            }
            if (LittleUtil.equalsNull(birth)) {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "请点击日历图标选择您的出生日期！");
                return;
            }
            if (LittleUtil.equalsNull(avatar)) {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "请上传您的头像！");
                return;
            }
        } catch (NullPointerException e) {
            AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "请将以上信息填写完整！");
            return;
        }

        File favicon = new File(FAVICON + email + ".png");
        System.out.println(favicon.getAbsolutePath());
        try {
            if (!favicon.exists() && favicon.createNewFile()) {
                ImageIO.write(SwingFXUtils.fromFXImage(new Image("file:" + file.getAbsolutePath()), null),
                        "png", favicon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = new User(email, password, nickname, gender, birth, FAVICON);

        try {
            boolean checkResult = chatController.checkEmail(email);
            if (!checkResult) {
                boolean insertResult = chatController.addNewUser(user);
                if (insertResult) {
                    AlertMaker.showMaterialDialog(stackPane, newAccountPane, "注册成功", "恭喜您注册成功，您现在可以进行登录了");
                }
            } else {
                AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "该邮箱已注册");
            }
        } catch (RemoteException | SQLException e) {
            //fixme 图片若存在出现异常
            AlertMaker.showMaterialAlert(stackPane, newAccountPane, "注册失败", "服务器出现异常，暂时无法注册，请见谅~");
        }
    }
}

