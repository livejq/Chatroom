package util;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;

/**
 * 定时任务列表
 * @author livejq
 * @since 2020/5/2
 **/
public class CustomEmojiTask extends ScheduledService<Integer> {

    private Pane emojiList;
    private Integer emojiHover;

    public void setEmojiHover(Integer emojiHover) {
        this.emojiHover = emojiHover;
    }

    public void setEmojiList(Pane emojiList) {
        this.emojiList = emojiList;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() {
                if (emojiHover == 0 && emojiList.isVisible()) {
                    Platform.runLater(() -> emojiList.setVisible(false));
                }

                return null;
            }
        };
    }
}
