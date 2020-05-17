package ui.emojis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import connector.ServerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ①导入emoji实体
 * ②管理emoji各类集合
 * ③转化不同的emoji表现形式(简称、unicode、hex)
 * ④解析分离字符串中的emoji和文本
 *
 * @author livejq
 * @since 2020/4/26
 **/
public class EmojiHandler {
    private static final String path = "/emoji.json";
    private static final EmojiHandler instance = new EmojiHandler();

    private Map<String, Emoji> emojiMap;
    private final HashMap<String, String> shortNameToUnicode = new HashMap<>();
    private final HashMap<String, String> unicodeToShortname = new HashMap<>();
    private final HashMap<String, String> shortnameToHex = new HashMap<>();

    Gson gson = new Gson();
    //正则匹配模式
    private Pattern UNICODE_PATTERN;
    private Pattern SHORTNAME_PATTERN;

    private EmojiHandler() {
        loadEmoji();
    }

    public static EmojiHandler getInstance() {
        return instance;
    }

    public Map<String, Emoji> getEmojiMap() {
        return emojiMap;
    }

    /**
     * 导入emoji表情，并初始化emoji管理器
     */
    private void loadEmoji() {

        JsonReader reader;
		reader = new JsonReader(new InputStreamReader(this.getClass().getResourceAsStream(path)));
		emojiMap = gson.fromJson(reader, new TypeToken<Map<String, Emoji>>() {
		}.getType());

		emojiMap.forEach((name, entry) -> {

            String shortName = entry.getShortname();
            //json文本中的unicode是hex形式的
            String hex = entry.getUnicode();
            String unicode = null;
            if (hex != null && !hex.isEmpty()) {
                unicode = convert(hex);
                //重新设置Emoji对象中的unicode和hex
                entry.setUnicode(unicode);
                entry.setHex(hex);
            }
            //logger.debug("{}", entry);
            if (shortName == null || shortName.isEmpty() || unicode == null || unicode.isEmpty()) {
                return;
            }
            shortNameToUnicode.put(shortName, unicode);
            unicodeToShortname.put(unicode, shortName);
            shortnameToHex.put(unicode, hex);
        });

        //使用分隔符|(正则表达式中或的意思)将各自的集合元素连成字符串，用于模式匹配
        SHORTNAME_PATTERN = Pattern
                .compile(String.join("|", shortNameToUnicode.keySet().stream().collect(Collectors.toList())));
        UNICODE_PATTERN = Pattern.compile(String.join("|",
                unicodeToShortname.keySet().stream().map(u -> Pattern.quote(u)).collect(Collectors.toList())));
    }

    /**
     * 根据关键字搜索出对应简称的emoji集合
     *
     * @param keywords 搜索框中输入的关键字
     * @return java.util.List<ui.emojis.Emoji>
     */
    public List<Emoji> search(String keywords) {

        return emojiMap.values().stream()
                .filter(emoji -> emoji.getShortname().contains(keywords))
                .collect(Collectors.toList());
    }

    /**
     * 将字符串中的emoji与文字解析分离(emoji以简称形式表现)
     *
     * @param input 字符串
     * @return java.util.Queue<java.lang.Object>
     */
    public Queue<Object> toEmojiAndText(String input) {
        Queue<Object> queue = new LinkedList<>();
        Matcher matcher = SHORTNAME_PATTERN.matcher(input);
        int lastEnd = 0;
        while (matcher.find()) {
            String lastText = input.substring(lastEnd, matcher.start());
            if (!lastText.isEmpty())
                queue.add(lastText);
            String m = matcher.group();
            emojiMap.values().stream().filter(entry -> entry.getShortname().equals(m)).forEach(entry -> {
                if (entry.getHex() != null && !entry.getHex().isEmpty()) {
                    queue.add(entry);
                }
            });
            lastEnd = matcher.end();
        }
        String lastText = input.substring(lastEnd);
        if (!lastText.isEmpty())
            queue.add(lastText);
        return queue;
    }

    /**
     * 字符串中表情为emoji简称形式的转化成Unicode形式的emoji
     *
     * @param str emoji简称
     * @return java.lang.String
     */
    public String shortNameToUnicode(String str) {
        String output = replaceWithFunction(str, SHORTNAME_PATTERN, (shortName) -> {
            //函数式接口实现
            if (shortName == null || shortName.isEmpty() || (!shortNameToUnicode.containsKey(shortName))) {
                return shortName;
            }
            if (shortNameToUnicode.get(shortName).isEmpty()) {
                return shortName;
            }
            String unicode = shortNameToUnicode.get(shortName).toUpperCase();
            return convert(unicode);
        });

        return output;
    }

    /**
     * 字符串中表情为unicode形式的转化成简称形式的emoji
     *
     * @param str emoji的unicode表示
     * @return java.lang.String
     */
    public String unicodeToShortname(String str) {
        String output = replaceWithFunction(str, UNICODE_PATTERN, (unicode) -> {
            if (unicode == null || unicode.isEmpty() || (!unicodeToShortname.containsKey(unicode))) {
                return unicode;
            }
            return unicodeToShortname.get(unicode);
        });
        return output;
    }

    /**
     * 将字符串转化不同形式的emoji
     *
     * @param input   用户消息输入框中输入的字符串
     * @param pattern 匹配emoji
     * @param func
     * @return java.lang.String
     */
    private String replaceWithFunction(String input, Pattern pattern, Function<String, String> func) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = pattern.matcher(input);
        int lastEnd = 0;
        //find返回false则不分割
        while (matcher.find()) {
            //分割并保留文本而不是emoji码
            String lastText = input.substring(lastEnd, matcher.start());
            builder.append(lastText);
            //分割并保留emoji码,再讲不同形式的emoji转化成Unicode
            builder.append(func.apply(matcher.group()));
            lastEnd = matcher.end();
        }
        builder.append(input.substring(lastEnd));

        return builder.toString();
    }

    /**
     * emoji的字节转换，即hex转换成字节形式
     *
     * @param unicodeStr hex
     * @return java.lang.String
     */
    private String convert(String unicodeStr) {
        if (unicodeStr.isEmpty())
            return unicodeStr;
        String[] parts = unicodeStr.split("-");
        StringBuilder buff = new StringBuilder();
        for (String s : parts) {
            int part = Integer.parseInt(s, 16);
            if (part >= 0x10000 && part <= 0x10FFFF) {
                int hi = (int) (Math.floor((part - 0x10000) / 0x400) + 0xD800);
                int lo = ((part - 0x10000) % 0x400) + 0xDC00;
                buff.append(new String(Character.toChars(hi)) + new String(Character.toChars(lo)));
            } else {
                buff.append(new String(Character.toChars(part)));
            }
        }
        return buff.toString();
    }
}
