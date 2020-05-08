package ui.view;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * 实现文本中每个字符的间距
 * @author livejq
 * @since 2020/5/1
 **/
public class LetterSpaceText extends FlowPane {
    private Font font;
    private Color fill;

    public LetterSpaceText(String text, double hGap, double vGap) {
        setText(text);
        setHgap(hGap);
        setVgap(vGap);
    }

    public void setText(String s) {
        getChildren().clear();
        for (int i = 0; i < s.length(); i++) {
            Text t = new Text("" + s.charAt(i));
            t.getStyleClass().add("bubbleMsgText");
            getChildren().add(t);
        }
        setFont(this.font);
        setFill(this.fill);
    }

    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
            for (Node t : getChildren()) {
                ((Text) t).setFont(font);
            }
        }
    }

    public void setFill(Color fill) {
        if(fill != null) {
            this.fill = fill;
            for (Node t : getChildren()) {
                ((Text) t).setFill(fill);
            }
        }
    }
}