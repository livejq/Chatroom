package util;

import enums.StrengthEnum;


/**
 * 密码强度检测工具
 * @author livejq
 * @since 2020/4/23
 **/
public class CheckStrength {

    /**
     * NUM 数字
     * LOWER_CASE 小写字母
     * UPPER_CASE 大写字母
     * OTHER_CHAR 特殊字符
     */
    private static final int NUM = 1;
    private static final int LOWER_CASE = 2;
    private static final int UPPER_CASE = 3;
    private static final int OTHER_CHAR = 4;

    //简单的密码字典
    private final static String[] DICTIONARY = {"password", "abc123", "iloveyou", "adobe123", "123123", "sunshine",
            "1314520", "a1b2c3", "123qwe", "aaa111", "qweasd", "admin", "passwd"};

    /**
     * @Author livejq
     * @Date 2020/4/24
     * @param character
     * @Description 检查字符类型，包括数字、大写字母、小写字母和其他字符
     * @return int
     */
    private static int checkCharacterType(char character) {
        if (character >= 48 && character <= 57) {
            return NUM;
        }
        if (character >= 65 && character <= 90) {
            return LOWER_CASE;
        }
        if (character >= 97 && character <= 122) {
            return UPPER_CASE;
        }

        return OTHER_CHAR;
    }

    /**
     * @Author livejq
     * @Date 2020/4/24
     * @param passwd
     * @param type
     * @Description 按不同类型计算密码的数量
     * @return int
     */
    private static int countLetter(String passwd, int type) {
        int count = 0;
        if (!LittleUtil.equalsNull(passwd)) {
            for (char c : passwd.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @Author livejq
     * @Date 2020/4/24
     * @param passwd
     * @Description 检查密码的强度
     * @return int
     */
    public static int checkPasswordStrength(String passwd) {
        if (LittleUtil.equalsNull(passwd)) {
            return 0;
        }
        int len = passwd.length();
        int level = 0;

        //加分项
        //判断密码是否含有数字有level++
        if (countLetter(passwd, NUM) > 0) {
            level++;
        }
        //判断密码是否含有小写字母有level++
        if (countLetter(passwd, LOWER_CASE) > 0) {
            level++;
        }
        //判断密码是否还有大写字母有level++
        if (len > 4 && countLetter(passwd, UPPER_CASE) > 0) {
            level++;
        }
        //判断密码是否还有特殊字符有level++
        if (len > 6 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }
        //2种类型组合
        if (len > 4 && countLetter(passwd, NUM) > 0 && countLetter(passwd, LOWER_CASE) > 0
                || countLetter(passwd, NUM) > 0 && countLetter(passwd, UPPER_CASE) > 0
                || countLetter(passwd, NUM) > 0 && countLetter(passwd, OTHER_CHAR) > 0
                || countLetter(passwd, LOWER_CASE) > 0 && countLetter(passwd, UPPER_CASE) > 0
                || countLetter(passwd, LOWER_CASE) > 0 && countLetter(passwd, OTHER_CHAR) > 0
                || countLetter(passwd, UPPER_CASE) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }
        //3种类型组合
        if (len > 6 && countLetter(passwd, NUM) > 0 && countLetter(passwd, LOWER_CASE) > 0
                && countLetter(passwd, UPPER_CASE) > 0 || countLetter(passwd, NUM) > 0
                && countLetter(passwd, LOWER_CASE) > 0 && countLetter(passwd, OTHER_CHAR) > 0
                || countLetter(passwd, NUM) > 0 && countLetter(passwd, UPPER_CASE) > 0
                && countLetter(passwd, OTHER_CHAR) > 0 || countLetter(passwd, LOWER_CASE) > 0
                && countLetter(passwd, UPPER_CASE) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }
        //4种类型组合
        if (len > 8 && countLetter(passwd, NUM) > 0 && countLetter(passwd, LOWER_CASE) > 0
                && countLetter(passwd, UPPER_CASE) > 0 && countLetter(passwd, OTHER_CHAR) > 0) {
            level++;
        }
        //2种类型组合且每种类型长度大于等于3或者2
        if (len > 6 && countLetter(passwd, NUM) >= 3 && countLetter(passwd, LOWER_CASE) >= 3
                || countLetter(passwd, NUM) >= 3 && countLetter(passwd, UPPER_CASE) >= 3
                || countLetter(passwd, NUM) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
                || countLetter(passwd, LOWER_CASE) >= 3 && countLetter(passwd, UPPER_CASE) >= 3
                || countLetter(passwd, LOWER_CASE) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2
                || countLetter(passwd, UPPER_CASE) >= 3 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }
        //3种类型组合且每种类型长度大于等于2或者3
        if (len > 8 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, LOWER_CASE) >= 2
                && countLetter(passwd, UPPER_CASE) >= 2 || countLetter(passwd, NUM) >= 2
                && countLetter(passwd, LOWER_CASE) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2
                || countLetter(passwd, NUM) >= 2 && countLetter(passwd, UPPER_CASE) >= 2
                && countLetter(passwd, OTHER_CHAR) >= 2 || countLetter(passwd, LOWER_CASE) >= 2
                && countLetter(passwd, UPPER_CASE) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }
        //4种类型组合且每种类型长度大于等于2
        if (len > 10 && countLetter(passwd, NUM) >= 2 && countLetter(passwd, LOWER_CASE) >= 2
                && countLetter(passwd, UPPER_CASE) >= 2 && countLetter(passwd, OTHER_CHAR) >= 2) {
            level++;
        }
        //特殊字符大于等于3
        if (countLetter(passwd, OTHER_CHAR) >= 3) {
            level++;
        }
        //特殊字符大于等于6
        if (countLetter(passwd, OTHER_CHAR) >= 6) {
            level++;
        }
        //长度大于12和大于16
        if (len > 12) {
            level++;
            if (len >= 16) {
                level++;
            }
        }

        //扣分项
        if ("abcdefghijklmnopqrstuvwxyz".indexOf(passwd) > 0 || "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(passwd) > 0) {
            level--;
        }
        if ("qwertyuiop".indexOf(passwd) > 0 || "asdfghjkl".indexOf(passwd) > 0 || "zxcvbnm".indexOf(passwd) > 0) {
            level--;
        }
        if (LittleUtil.isNumeric(passwd) && ("01234567890".indexOf(passwd) > 0 || "09876543210".indexOf(passwd) > 0)) {
            level--;
        }
        if (countLetter(passwd, NUM) == len || countLetter(passwd, LOWER_CASE) == len
                || countLetter(passwd, UPPER_CASE) == len) {
            level--;
        }
        if (len % 2 == 0) { //aaabbb
            String part1 = passwd.substring(0, len / 2);
            String part2 = passwd.substring(len / 2);
            if (part1.equals(part2)) {
                level--;
            }
            if (LittleUtil.isCharEqual(part1) && LittleUtil.isCharEqual(part2)) {
                level--;
            }
        }
        if (len % 3 == 0) { //ababab
            String part1 = passwd.substring(0, len / 3);
            String part2 = passwd.substring(len / 3, len / 3 * 2);
            String part3 = passwd.substring(len / 3 * 2);
            if (part1.equals(part2) && part2.equals(part3)) {
                level--;
            }
        }
/*        if (StringUtils.isNumeric(passwd) && len >= 6 && len <= 8) { //19990101 or 990101
            int year = 0;
            if (len == 6 || len == 8) {
                year = Integer.parseInt(passwd.substring(0, len - 4));
            }
            int size = StringUtils.sizeOfInt(year);
            int month = Integer.parseInt(passwd.substring(size, size + 2));
            int day = Integer.parseInt(passwd.substring(size + 2, len));
            if (year >= 1950 && year < 2050 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                level--;
            }
        }*/
        if (DICTIONARY != null && DICTIONARY.length > 0) {//dictionary
            for (String dic : DICTIONARY) {
                if (passwd.equals(dic) || dic.contains(passwd)) {
                    level--;
                    break;
                }
            }
        }
        if (len <= 6) {
            level--;
            if (len <= 4) {
                level--;
                if (len <= 3) {
                    level = 0;
                }
            }
        }
        if (LittleUtil.isCharEqual(passwd)) {
            level = 0;
        }
        if (level < 0) {
            level = 0;
        }
        return level;
    }

    /**
     * @Author livejq
     * @Date 2020/4/24
     * @param passwd
     * @Description 获得密码强度等级
     * @return enums.StrengthEnum
     */
    public static StrengthEnum getPasswordLevel(String passwd) {
        int level = checkPasswordStrength(passwd);
        switch (level) {
            case 0:
            case 1:
            case 2:
            case 3:
                return StrengthEnum.EASY;
            case 4:
            case 5:
            case 6:
                return StrengthEnum.MIDDLE;
            case 7:
            case 8:
            case 9:
                return StrengthEnum.STRONG;
            case 10:
            case 11:
            case 12:
                return StrengthEnum.VERY_STRONG;
            default:
                return StrengthEnum.EXTREMELY_STRONG;
        }
    }
}


