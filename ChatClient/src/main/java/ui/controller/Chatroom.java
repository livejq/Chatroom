package ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import connector.ServerConnector;
import control.ChatController;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import observer.ChatObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.emojis.EmojiDisplayer;
import ui.view.LaunchTray;
import ui.view.LoaderMaker;
import util.CustomEmojiTask;
import util.LittleUtil;
import util.RegexUtil;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 聊天室主界面
 * @author livejq
 * @since 2020/4/20
 **/
public class Chatroom implements ChatObserver {

    private ChatObserver chatObserver;
    private ChatController chatController;

    //0,未停留；1，停留
    private volatile Integer emojiHover = 0;
    private User user;
    private double xPressed;
    private double yPressed;
    private double xStage;
    private double yStage;
    private Stage primaryStage;
    private CustomEmojiTask customEmojiTask;
    //true,已展示；false，已隐藏
    private volatile boolean funcSideStatus;
    private List<User> onlineUsers;
    private static final Logger logger = LoggerFactory.getLogger(Chatroom.class.getName());

    @FXML
    private StackPane mainPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField searchUser;

    @FXML
    private ScrollPane showClientListScroll;

    @FXML
    private VBox showClientListBox;

    @FXML
    private ScrollPane searchClientListScroll;

    @FXML
    private VBox searchClientListBox;

    @FXML
    private AnchorPane leftAnchorPane;

    @FXML
    private AnchorPane rightAnchorPane;

    @FXML
    private JFXTextArea msgArea;

    @FXML
    private VBox chatBox;

    @FXML
    private ScrollPane msgScrollPane;

    @FXML
    private Pane emojiList;

    @FXML
    private JFXButton attachFile;

    @FXML
    private JFXButton btnSend;

    @FXML
    private JFXButton btnEmoji;

    @FXML
    private AnchorPane hiddenSidePane;

    @FXML
    private MaterialDesignIconView sideMenu;

    @FXML
    private AnchorPane funcSide;

    @FXML
    private Circle avatar;

    @FXML
    private Label sideEmailLabel;

    @FXML
    private Label sideNameLabel;

    @FXML
    private Label linkToAbout;

    @FXML
    private void initialize() {
        //fixme 为搜索用户文本框添加监听(会重复执行createUserBox方法，待解决)
        /*searchUser.textProperty().addListener(x -> {

        });*/

        //默认隐藏侧边栏
        hideAnimation();
        linkToAbout.setOnMouseClicked(event2 -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.livejq.top"));
            } catch (IOException | URISyntaxException e) {
                logger.error("链接打开失败...", e);
            }
        });

        //给每个表情添加点击事件
        for (Node text : emojiList.getChildren()) {
            text.setOnMouseClicked(event -> {
                msgArea.setText(msgArea.getText() + " " + ((Text) text).getText());
                emojiList.setVisible(false);
            });
        }

        //控件参数绑定（聊天面板高度 -> 外层滚动面板高度）使得实时显示新消息
        msgScrollPane.vvalueProperty().bind(chatBox.heightProperty());
        showClientListScroll.vvalueProperty().bind(showClientListBox.heightProperty());

        //初始化Emoji显示面板，提前将表情加载进来，避免卡顿
        FXMLLoader loader2 = LoaderMaker.createLoader("../fxml/EmojiSelector.fxml");
        try {
            VBox emojiPane = loader2.load();
            EmojiSelector emojiSelector = loader2.getController();
            emojiSelector.setMsgArea(msgArea);
            emojiSelector.setEmojiList(emojiList);
            emojiList.getChildren().add(emojiPane);
        } catch (IOException e) {
            logger.error("创建fxml失败...[{}]", loader2, e);
        }

        //设置Emoji面板触发延时关闭任务
        customEmojiTask = new CustomEmojiTask();
        customEmojiTask.setEmojiList(emojiList);
        customEmojiTask.setEmojiHover(emojiHover);
        customEmojiTask.setPeriod(Duration.seconds(1));
        customEmojiTask.start();

        //调节滚动面板
        msgScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        showClientListScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        searchClientListScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatBox.setCache(true);

        //TODO 文件传输，需要搭建VSFTP文件服务器，暂时不搞
        attachFile.setTooltip(new Tooltip("待开发哦~"));
        btnSend.setTooltip(new Tooltip("发送消息"));
        btnEmoji.setTooltip(new Tooltip("Emoji表情"));
    }

    @FXML
    private void handleEmojiEntered(MouseEvent event) {
        emojiHover = 1;
        customEmojiTask.setEmojiHover(emojiHover);
        if (!emojiList.isVisible()) {
            emojiList.setVisible(true);
        }
    }

    @FXML
    private void handleEmojiExited(MouseEvent event) {
        emojiHover = 0;
        customEmojiTask.setEmojiHover(emojiHover);
    }

    @FXML
    private void handleEmojiPaneEntered(MouseEvent event) {
        emojiHover = 1;
        customEmojiTask.setEmojiHover(emojiHover);
    }

    @FXML
    private void handleEmojiPaneExited(MouseEvent event) {
        emojiHover = 0;
        customEmojiTask.setEmojiHover(emojiHover);
    }

    @FXML
    private void sendAction(ActionEvent event) {
        send();
    }

    private void send() {
        String msg = msgArea.getText().trim();
        try {
            if (LittleUtil.equalsNull(msg)) return;
            chatController.notifyAllClients(user, msg);
        } catch (NullPointerException | IOException e) {
            logger.error("消息发送失败...[{}]", msg, e);
        }
        msgArea.setText("");
        msgArea.requestFocus();
    }

    @FXML
    private void showFuncSide(MouseEvent event) {
        if (!funcSideStatus) {
            showAnimation();
        }
    }

    @FXML
    private void hideFuncSide(MouseEvent event) {
        if (funcSideStatus) {
            hideAnimation();
        }
    }

    @FXML
    private void handleSearchField(KeyEvent event) {
        String key = searchUser.getText();
        searchClientListBox.getChildren().clear();
        if (LittleUtil.equalsNull(key)) {
            showClientListScroll.setVisible(true);
            searchClientListScroll.setVisible(false);
        } else if(RegexUtil.matchMail(key)) {
            //每次添加前都需要清空
            showClientListScroll.setVisible(false);
            searchClientListScroll.setVisible(true);
            //将关键字与用户名和其邮箱进行匹配，将满足条件的列出来
            List<User> searchResult = onlineUsers.stream()
                    .filter(user1 -> user1.getEmail().contains(key))
                    .collect(Collectors.toList());
            searchResult.forEach(user1 -> createUserBox(user1, searchClientListBox));
        } else if (RegexUtil.matchName(key)) {
            showClientListScroll.setVisible(false);
            searchClientListScroll.setVisible(true);
            List<User> searchResult = onlineUsers.stream()
                    .filter(user1 -> user1.getNickname().contains(key))
                    .collect(Collectors.toList());
            searchResult.forEach(user1 -> createUserBox(user1, searchClientListBox));
        }
    }

    @Override
    public boolean update(User user, String message) {
        FXMLLoader loader;
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        //判断该消息的发送者是否为自己
        if (this.user.getEmail().equals(user.getEmail())) {
            loader = LoaderMaker.createLoader("../fxml/BubbleSelf.fxml");
            try {
                HBox hBox = loader.load();
                BubbleSelf bubbleSelf = loader.getController();
                TextFlow msgRight = new TextFlow(EmojiDisplayer.createEmojiAndTextNode(message));
                msgRight.getStyleClass().add("emojiTextPane");
                bubbleSelf.getBubbleFlow().getChildren().add(msgRight);
                Circle img = bubbleSelf.getAvatar();
                String path = "";
                try {
                    path = new File(user.getAvatar() + user.getEmail() + ".png").toURI().toString();
                    img.setFill(new ImagePattern(new Image(path)));
                } catch (Exception e) {
                    logger.error("没有找到相应的用户头像...{}", path, e);
                }
                bubbleSelf.getBubbleTimeLabel().setText(time.format(new Date()));
                bubbleSelf.getBubbleNameLabel().setText(user.getNickname());
                HBox lineBoxRight = new HBox(hBox);
/*                lineBoxRight.setStyle("-fx-border-width: 1px;" +
                        "-fx-border-style: solid inside;"+
                        "-fx-border-color: red;");*/
                lineBoxRight.getStyleClass().add("lineBoxRight");
                //要调用UI线程才能更新客户端界面
                Platform.runLater(() -> chatBox.getChildren().add(lineBoxRight));
            } catch (IOException e) {
                logger.error("此消息发送异常...", e);
            }
        } else {
            loader = LoaderMaker.createLoader("../fxml/BubblePublic.fxml");
            try {
                HBox hBox = loader.load();
                BubblePublic bubblePublic = loader.getController();
                TextFlow msgLeft = new TextFlow(EmojiDisplayer.createEmojiAndTextNode(message));
                msgLeft.getStyleClass().add("emojiTextPane");
                bubblePublic.getBubbleFlow().getChildren().add(msgLeft);
                Circle img = bubblePublic.getAvatar();
                String path = "";
                try {
                    path = new File(user.getAvatar() + user.getEmail() + ".png").toURI().toString();
                    img.setFill(new ImagePattern(new Image(path)));
                } catch (Exception e) {
                    logger.error("没有找到相应的用户头像...{}", path, e);
                }
                bubblePublic.getBubbleTimeLabel().setText(time.format(new Date()));
                bubblePublic.getBubbleNameLabel().setText(user.getNickname());
                HBox lineBoxLeft = new HBox(hBox);
                lineBoxLeft.getStyleClass().add("lineBoxLeft");
                Platform.runLater(() -> chatBox.getChildren().add(lineBoxLeft));
            } catch (IOException e) {
                logger.error("此消息发送异常...", e);
            }
        }

        return true;
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public void updateOnline(List<String> clientList) {
        logger.info("当前在线用户总数为：" + clientList.size());
        onlineUsers = new ArrayList<>();
        Platform.runLater(() -> showClientListBox.getChildren().clear());
        //一个邮箱账号标识一个用户
        clientList.stream().filter(Objects::nonNull).forEach(email -> {
            if (!email.equals(user.getEmail())) {
                try {
                    User client = chatController.get(email);
                    if (client != null) {
                        createUserBox(client, showClientListBox);
                        onlineUsers.add(client);
                    }
                } catch (RemoteException | SQLException e) {
                    logger.error("更新在线用户发生异常");
                }
            }
        });
    }


    private void createUserBox(User user, VBox clientBox) {
        try {
            FXMLLoader loader = LoaderMaker.createLoader("../fxml/OnlinePane.fxml");
            HBox onlineContainer = loader.load();
            OnlinePane onlinePane = loader.getController();
            Circle img = onlinePane.getAvatar();
            String path = "";
            try {
                path = new File(user.getAvatar() + user.getEmail() + ".png").toURI().toString();
                img.setFill(new ImagePattern(new Image(path)));
            } catch (Exception e) {
                logger.error("没有找到相应的用户头像...{}", path, e);
            }
            onlinePane.getOnlineNameLabel().setText(user.getNickname());
            Platform.runLater(() -> clientBox.getChildren().add(onlineContainer));
        } catch (IOException e) {
            e.printStackTrace();
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
    private void handleWindowMinimized(MouseEvent event) {
        primaryStage.setIconified(true);
    }

    @FXML
    private void handleWindowClosed(MouseEvent event) {
//        Runtime.getRuntime().exit(0);
//        ((Stage) mainPane.getScene().getWindow()).close();
        //隐藏到托盘
        primaryStage.close();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //设置消息发送ctrl+Enter快捷键
        KeyCombination keyCodeCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        primaryStage.getScene().getAccelerators().put(keyCodeCombination, () -> {
            send();
        });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Circle getAvatar() {
        return avatar;
    }

    public Label getSideEmailLabel() {
        return sideEmailLabel;
    }

    public Label getSideNameLabel() {
        return sideNameLabel;
    }

    public void setChatObserver(ChatObserver chatObserver) {
        this.chatObserver = chatObserver;
    }

    private void showAnimation() {
        double startWidth = funcSide.getWidth();
        Animation showFuncSide = new Transition() {
            {
                setCycleDuration(Duration.millis(450));
            }

            protected void interpolate(double frac) {
                final double curWidth = startWidth * frac;
                //提前展示，避免卡顿感
                funcSide.setVisible(true);
                funcSide.setTranslateX(-startWidth + curWidth);
            }
        };
        showFuncSide.play();
        funcSideStatus = true;
    }

    private void hideAnimation() {
        double startWidth = funcSide.getWidth();
        Animation hideFuncSide = new Transition() {
            {
                setCycleDuration(Duration.millis(350));
            }

            protected void interpolate(double frac) {
                final double curWidth = startWidth * (1.0 - frac);
                funcSide.setTranslateX(-startWidth + curWidth);
            }
        };
        hideFuncSide.onFinishedProperty().set(actionEvent -> {
            funcSide.setVisible(false);
        });
        hideFuncSide.play();
        funcSideStatus = false;
    }

    public void initAfter() {
        //更新聊天界面的用户列表
        try {
            chatController = ServerConnector.getServerConnector().getChatController();
            logger.info(user.getEmail() + " 用户登录成功！正在更新聊天用户列表...");
            chatController.updateClientList();
        } catch (IOException e) {
            logger.error("初始化聊天室失败！", e);
        }
        //将聊天窗口绑定到托盘
        LaunchTray.bindStage(this.primaryStage, chatController, chatObserver, user.getEmail());
    }
}