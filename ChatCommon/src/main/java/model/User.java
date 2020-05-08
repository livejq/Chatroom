package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户对象模板
 * @author livejq
 * @since 2020/4/08
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 42L;

    private String email;
    private String password;
    private String nickname;
    private String gender;
    private String birth;
    private String avatar;
}
