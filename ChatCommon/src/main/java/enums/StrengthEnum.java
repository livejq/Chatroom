package enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 密码强度等级
 * @author livejq
 * @since 2020/4/23
 **/
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StrengthEnum {
    EASY(1, "弱"), MIDDLE(2, "中等"), STRONG(3, "强"), VERY_STRONG(4, "非常强"), EXTREMELY_STRONG(5, "逆天了");

    private final Integer id;
    private final String description;
}
