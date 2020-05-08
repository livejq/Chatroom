package enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 获取单选按钮id所对应的选项
 * @author livejq
 * @since 2020/4/16
 **/
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GenderEnum {

    MALE(1, "rdbMan", "男"),
    FEMALE(2, "rdbWoman", "女"),
    SECRET(3, "rdbNone", "保密");

    private final Integer id;
    private final String fxId;
    private final String description;
}
