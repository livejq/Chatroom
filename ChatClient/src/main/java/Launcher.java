import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.controller.Login;

import java.io.IOException;

/**
 * 客户端启动器
 * @author livejq
 * @since 2020/4/13
 **/
public class Launcher extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Launcher.class.getName());
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/fxml/Login.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Login login = loader.getController();
            primaryStage.setScene(scene);
            login.setPrimaryStage(primaryStage);
            primaryStage.centerOnScreen();
            primaryStage.getIcons().add(new Image("/util/images/icons/chatroom.png"));
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException e) {
            logger.error("FXML文件加载错误！", e);
        }
    }
}
