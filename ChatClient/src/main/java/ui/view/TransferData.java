package ui.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.User;
import ui.controller.LoginProgress;

import java.io.IOException;
import java.util.Map;


/**
 * 图层间数据传输
 * @author livejq
 * @since 2020/4/26
 **/
public class TransferData {

    /**
     * 加载LoginProgress图层并传递数据
     *
     * @param path        fxml文件路径
     * @param parentPane  StackPane容器
     * @param removedPane 需要从容器中移除的面板
     * @param message     向新面板传递数据
     * @return void
     */
    public static void loadPaneByPathWithData(String path, StackPane parentPane, Pane removedPane,
                                              Map<String, Object> message, Map<String, Pane> storage) throws IOException {
        FXMLLoader loader = LoaderMaker.createLoader(path);
        Pane childrenPane = loader.load();
        //必须要在load()方法之后调用controller,避免空指针
        LoginProgress loginProgress = loader.getController();
        loginProgress.setUser((User) message.get("user"));
        storage.put(removedPane.getId(), removedPane);
        parentPane.getChildren().remove(removedPane);
        parentPane.getChildren().add(childrenPane);
    }
}
