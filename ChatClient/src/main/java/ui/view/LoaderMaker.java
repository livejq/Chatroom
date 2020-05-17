package ui.view;

import javafx.fxml.FXMLLoader;

/**
 * 视图加载器
 * @author livejq
 * @since 2020/4/28
 **/
public class LoaderMaker {

    public FXMLLoader createLoader(String path) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(path));

        return loader;
    }
}
