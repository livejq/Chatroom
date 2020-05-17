package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串常规处理
 * @author livejq
 * @since 2020/4/23
 **/
public class LittleUtil {
    private final static int[] SIZE_TABLE = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
            Integer.MAX_VALUE};

    /**
     * 计算一个整数的位数
     * @param number 一串数字
     * @return int
     */
    public static int sizeOfInt(int number) {
        for (int i = 0; ; i++)
            if (number <= SIZE_TABLE[i]) {
                return i + 1;
            }
    }

    /**
     * 判断字符串的每个字符是否相等
     * @param str
     * @return boolean
     */
    public static boolean isCharEqual(String str) {
        return str.replace(str.charAt(0), ' ').trim().length() == 0;
    }

    /**
     * 确定字符串是否为数字
     * @param str
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否为空格、空串或null
     * @param str
     * @return boolean
     */
    public static boolean equalsNull(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0 || str.equalsIgnoreCase("null")) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 保留两位小数
     * @param value
     * @return java.lang.String
     */
    public static String format(double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);

        return df.format(value);
    }

    /**
     * 排除数字和大小写字母
     * @param character
     * @return int
     */
    public static boolean isOtherType(char character) {
        boolean otherType = true;
        if (character >= 48 && character <= 57) {
            otherType = false;
            return otherType;
        }
        if (character >= 65 && character <= 90) {
            otherType = false;
            return otherType;
        }
        if (character >= 97 && character <= 122) {
            otherType = false;
            return otherType;
        }

        return otherType;
    }

    /**
     * 判断是否含有除数字和大小写字母外的其他字符,有则返回true
     * @param str
     * @return boolean
     */
    public static boolean checkOtherType(String str) {
        for (char c : str.toCharArray()) {
            if (isOtherType(c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断字符中是否包含特殊字符
     * @param str
     * @return boolean
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.find();
    }
}


