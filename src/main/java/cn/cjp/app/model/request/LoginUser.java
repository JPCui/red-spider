package cn.cjp.app.model.request;

import lombok.Data;

/**
 * 当前登陆用户
 *
 * @author sucre
 */
@Data
public class LoginUser {

    private String userId;

    private String nickName;

    private int gender;

    /**
     * 头像
     */
    private String avatar;

}
