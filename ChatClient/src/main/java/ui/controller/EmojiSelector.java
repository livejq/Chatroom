package ui.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ui.emojis.Emoji;
import ui.emojis.EmojiDisplayer;
import ui.emojis.EmojiHandler;
import util.LittleUtil;

import java.util.List;

/**
 * Emoji表情控制
 * @author livejq
 * @since 2020/4/25
 **/
public class EmojiSelector {

	private TextArea msgArea;
	private Pane emojiList;

	@FXML
	private ScrollPane showScrollPane;
	@FXML
	private FlowPane showFlowPane;
	@FXML
	private JFXTextField searchTextField;
	@FXML
	private ScrollPane searchScrollPane;
	@FXML
	private FlowPane searchFlowPane;

	public void setMsgArea(TextArea msgArea) {
		this.msgArea = msgArea;
	}

	public void setEmojiList(Pane emojiList) {
		this.emojiList = emojiList;
	}

	@FXML
	public void initialize() {
		//设置emoji显示界面
		showScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		showFlowPane.setHgap(5);
		showFlowPane.setVgap(5);
		//设置搜索结果界面
		searchScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		searchFlowPane.setHgap(5);
		searchFlowPane.setVgap(5);
		//搜索表情文本框监视器
		searchTextField.textProperty().addListener(x -> {
			String text = searchTextField.getText();
			if (LittleUtil.equalsNull(text) || text.length() < 2) {
				searchFlowPane.getChildren().clear();
				searchScrollPane.setVisible(false);
				showScrollPane.setVisible(true);
			} else {
				showScrollPane.setVisible(false);
				searchScrollPane.setVisible(true);
				List<Emoji> results = EmojiHandler.getInstance().search(text);
				searchFlowPane.getChildren().clear();
				results.forEach(emoji -> searchFlowPane.getChildren().add(addEmojiNodeListener(emoji)));
			}
		});
		init();
	}

	/**
	 * 初始化刷新emoji的显示
	 * @return void
	 */
	private void init() {
		Platform.runLater(() -> {
			EmojiHandler.getInstance().getEmojiMap().values()
					.forEach(emoji -> showFlowPane.getChildren().add(addEmojiNodeListener(emoji)));
			showScrollPane.requestFocus();
		});
	}

	/**
	 * 创建emoji节点stackPane，并给其添加事件监听器
	 * @param emoji Emoji表情对象
	 * @return javafx.scene.Node
	 */
	private Node addEmojiNodeListener(Emoji emoji) {
		Insets pad = new Insets(5, 5, 5, 5);
		Node stackPane = EmojiDisplayer.createEmojiNode(emoji, 24, pad);
		if (stackPane instanceof StackPane) {
			//设置光标手势
			stackPane.setCursor(Cursor.HAND);
			//鼠标停留时放大emoji表情
			ScaleTransition st = new ScaleTransition(Duration.millis(90), stackPane);
			//设置提示
			Tooltip tooltip = new Tooltip(emoji.getShortname());
			Tooltip.install(stackPane, tooltip);
			//设置光标的触发事件
			stackPane.setOnMouseEntered(e -> {
				stackPane.setEffect(new DropShadow());
				st.setToX(1.2);
				st.setToY(1.2);
				st.playFromStart();
				if (searchTextField.getText().isEmpty())
					searchTextField.setPromptText(emoji.getShortname());
			});
			//设置光标的离开事件
			stackPane.setOnMouseExited(e -> {
				stackPane.setEffect(null);
				st.setToX(1.);
				st.setToY(1.);
				st.playFromStart();
				
				
			});
			//设置光标的点击事件
			stackPane.setOnMouseClicked(e -> {
				//fixme 当用户光标在其输入的文本中时将无法在其想要的位置上插入emoji简称（总是插在输入框最末端）已解决
				String shortName = emoji.getShortname();
				//获取用户输入光标位置
				int pos = msgArea.caretPositionProperty().get();
//				System.out.println("pos:"+pos);
				String msg = msgArea.getText();
				//两种情况：光标后面有字或没字
				if (!LittleUtil.equalsNull(msg.substring(pos))) {
					String frontStr = msg.substring(0, pos);
					String behindStr = msg.substring(pos);
					msgArea.setText(frontStr + shortName + behindStr);
				}else {
					msgArea.setText(msg + shortName);
				}
				emojiList.setVisible(false);
			});
		}

		return stackPane;
	}

}
