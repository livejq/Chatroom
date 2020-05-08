package ui.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 图层切换工具
 * @author livejq
 * @since 2020/4/18
 **/
public class PaneLayer {

    private PaneLayer() {
    }

    private static class InnerStorage {
        static Map<String, Pane> storage = new HashMap<>(4);
    }

    public static Map<String, Pane> getStorage() {
        return InnerStorage.storage;
    }

    /**
     * 通过fxml文件加载窗口到StackPane
     *
     * @param path
     * @param parentPane
     * @param removedPane
     * @return void
     */
    public static void loadPaneByPath(String path, StackPane parentPane, Pane removedPane) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PaneLayer.class.getResource(path));
            Pane childrenPane = loader.load();
            getStorage().put(removedPane.getId(), removedPane);
            parentPane.getChildren().remove(removedPane);
            parentPane.getChildren().add(childrenPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过已加载过的窗口id重新调出窗口到StackPane
     *
     * @param id
     * @param removedPane
     * @return void
     */
    public static void loadPaneById(String id, Pane removedPane) {
        boolean isExist = getStorage().containsKey(id);
        if (!isExist) {
            getStorage().put(removedPane.getId(), removedPane);
        }
        StackPane parentPane = (StackPane) removedPane.getParent();
        parentPane.getChildren().remove(removedPane);
        parentPane.getChildren().add(getStorage().get(id));
    }
}
