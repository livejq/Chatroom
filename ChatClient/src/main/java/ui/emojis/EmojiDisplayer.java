package ui.emojis;

import java.util.Queue;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * ①为emoji实体集创建emoji图片节点
 * ②为包含emoji的字符串分离并创建emoji图片节点和文本节点
 * @author livejq
 * @since 2020/4/26
 **/
public class EmojiDisplayer {

	/**
	 * 将字符串解析分离成emoji图片节点和文本节点
	 * @param input 用户输入的字符串
	 * @return javafx.scene.Node[]
	 */
	public static Node[] createEmojiAndTextNode(String input) {
		Queue<Object> queue = EmojiHandler.getInstance().toEmojiAndText(input);
		Node[] nodes = new Node[queue.size()];
		int i = 0;
		while (!queue.isEmpty()) {
			Object ob = queue.poll();
			if (ob instanceof String) {
				String text = (String) ob;
				nodes[i++] = createTextNode(text);
			} else if (ob instanceof Emoji) {
				Emoji emoji = (Emoji) ob;
				Insets pad = new Insets(0, 3, 0, 3);
				nodes[i++] = createEmojiNode(emoji, 18, pad);
			}
		}

		return nodes;
	}

	/**
	 * 创建emoji图片节点
	 * @param emoji Emoji对象
	 * @param size 图片显示大小
	 * @param pad 图片间距
	 * @return javafx.scene.Node
	 */
	public static Node createEmojiNode(Emoji emoji, int size, Insets pad) {
		//将表情放到stackPane中
		StackPane stackPane = new StackPane();
		stackPane.setMaxSize(size, size);
		stackPane.setPrefSize(size, size);
		stackPane.setMinSize(size, size);
		stackPane.setPadding(pad);
		ImageView imageView = new ImageView();
		imageView.setFitWidth(size);
		imageView.setFitHeight(size);
		try {
			imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
			stackPane.getChildren().add(imageView);
		} catch (IllegalArgumentException e) {
			//fixme 得重新根据emoji图片目录生成对应的json文件
			//logger.info("未找到该图片资源!");
		}

		return stackPane;
	}

	/**
	 * 创建文本节点
	 * @param text 文本
	 * @return javafx.scene.Node
	 */
	private static Node createTextNode(String text) {
//		FlowPane textNode = new LetterSpaceText(text, 1.5, 3.0);
		Text textNode = new Text(text);
		textNode.getStyleClass().add("bubbleMsgText");

		return textNode;
	}

	/**
	 * 通过emoji的hex得到表情的路径
	 * @param hexStr emoji的hex表示
	 * @return java.lang.String
	 */
	private static String getEmojiImagePath(String hexStr) {
		return "/util/images/emoji/32/" + hexStr + ".png";
	}
}
