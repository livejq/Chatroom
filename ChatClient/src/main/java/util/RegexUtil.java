package util;

import java.util.regex.Pattern;

/**
 * 用户输入校验工具
 * @author livejq
 * @since 2020/4/23
 **/
public class RegexUtil {

    public static boolean validateEmailAddress(String emailAddr) {
        String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(emailAddr).matches();
    }

    public static boolean matchMail(String key) {
        String regex = "[_A-Za-z0-9-]+";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(key).matches();
    }

    public static boolean matchName(String key) {
        String regex = "[\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(key).matches();
    }
}
