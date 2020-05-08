package ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import connector.ServerConnector;
import control.ChatController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import mail.HtmlMail;
import ui.view.AlertMaker;
import ui.view.PaneLayer;
import util.LittleUtil;
import util.RegexUtil;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 正在登录动画
 * @author livejq
 * @since 2020/4/28
 **/
public class FindPassword {

    private ChatController chatController;
    private boolean confirmStatus;
    private HtmlMail sendHtmlMail;
    private String code;

    @FXML
    private AnchorPane findPasswordPane;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtCaptcha;

    @FXML
    private JFXTextField txtNewPassword;

    @FXML
    private JFXButton btnCaptcha;

    @FXML
    private void initialize() {
        chatController = ServerConnector.getServerConnector().getChatController();
        sendHtmlMail = new HtmlMail();
        //验证码启用数字键约束
        //fixme 还是无法约束中文，待修复
        txtCaptcha.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]+") && (event.getCode() != KeyCode.BACK_SPACE) && !event.getCharacter().matches("\\s")) {
                event.consume();
            }
        });
    }

    @FXML
    void btnReturnAction(ActionEvent event) {
        PaneLayer.loadPaneById("loginPane", findPasswordPane);
    }

    @FXML
    private void handleGetCaptcha(ActionEvent event) {
        String email = txtEmail.getText();
        if (LittleUtil.equalsNull(email) || !RegexUtil.validateEmailAddress(email) || email.length() > 50) {
            AlertMaker.showMaterialDialog((StackPane) findPasswordPane.getParent(), findPasswordPane, "获取失败", "请填写正确的邮箱地址！");
            txtEmail.requestFocus();
            return;
        }
        try {
            boolean result = chatController.checkEmail(email);
            if (!result) {
                AlertMaker.showMaterialDialog((StackPane) findPasswordPane.getParent(), findPasswordPane, "获取失败", "您还没有注册该邮箱呢！");
                txtEmail.requestFocus();
                return;
            }
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }
        Task<Void> captchaTask = captchaAnimation();
        btnCaptcha.textProperty().bind(captchaTask.messageProperty());
        btnCaptcha.setDisable(true);
        confirmStatus = true;
        new Thread(captchaTask).start();
        //fixme 点击获取验证码时会有卡顿
        code = sendHtmlMail.send(email);
    }

    @FXML
    private void handleSetNewPassword(ActionEvent event) {
        if (!confirmStatus) {
            AlertMaker.showMaterialDialog((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "请先获取验证码！");
            txtCaptcha.requestFocus();
            return;
        }

        String email, captcha, newPassword;
        //用户输入校验
        try {
            email = txtEmail.getText();
            captcha = txtCaptcha.getText();
            newPassword = txtNewPassword.getText();
            if (LittleUtil.equalsNull(email) || !RegexUtil.validateEmailAddress(email) || email.length() > 50) {
                AlertMaker.showMaterialAlert((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "请填写正确的邮箱地址！");
                txtEmail.requestFocus();
                return;
            }
            if (LittleUtil.equalsNull(captcha) || captcha.length() > 4 || !LittleUtil.isNumeric(captcha)) {
                AlertMaker.showMaterialAlert((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "请注意验证码格式！");
                txtCaptcha.requestFocus();
                return;
            }
            if (!captcha.equals(code)) {
                AlertMaker.showMaterialAlert((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "验证码错误！");
                txtCaptcha.requestFocus();
                return;
            }
            if (LittleUtil.equalsNull(newPassword) || newPassword.length() > 20) {
                AlertMaker.showMaterialAlert((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "密码长度不超过20位！");
                txtNewPassword.requestFocus();
                return;
            }
        } catch (NullPointerException e) {
            AlertMaker.showMaterialAlert((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改失败", "请将以上信息填写完整！");
            return;
        }


        try {
            boolean updateResult = chatController.updatePassword(email, newPassword);
            if (updateResult) {
                AlertMaker.showMaterialDialog((StackPane) findPasswordPane.getParent(), findPasswordPane, "修改成功", "您现在可以进行登录了");
            }
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建验证码倒计时动画
     * @return javafx.concurrent.Task<java.lang.Void>
     */
    private Task<Void> captchaAnimation() {
        return new Task<Void>() {

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("获取");
                btnCaptcha.setDisable(false);
                confirmStatus = false;
            }

            @Override
            protected Void call() throws Exception {
                System.out.println("验证码发送功能恢复中...");
                for (int i = 60; i > 0; i--) {
                    updateMessage((i - 1) + "秒");
                    TimeUnit.SECONDS.sleep(1);
                }
                System.out.println("验证码倒计时线程：" + Thread.currentThread().getName());

                return null;
            }
        };
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }
}
