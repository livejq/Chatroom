import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controller.Login;
import ui.view.LoaderMaker;

import java.io.IOException;

/**
 * 客户端启动器
 * @author livejq
 * @since 2020/4/13
 **/
public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = LoaderMaker.createLoader("../fxml/Login.fxml");
        Scene scene = new Scene(fxmlLoader.load());
        Login login = fxmlLoader.getController();
        primaryStage.setScene(scene);
        login.setPrimaryStage(primaryStage);
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image("ui/util/images/icons/chatroom.png"));
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
