package cn.cjp.app.config;

public class AppConfig {

    /**
     * M站TOKEN登录session
     **/
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    /**
     * M站登录用户回跳页面session
     **/
    public static final String LOGIN_NEXT_URL = "LOGIN_NEXT_URL";
    /**
     * M站登录用户session
     **/
    public static final String ACCESS_USER_ID = "ACCESS_USER_ID";
    /**
     * M站登录用户名session
     **/
    public static final String ACCESS_USER_NAME = "ACCESS_USER_NAME";
    /**
     * M站自动登陆过期时
     **/
    public static final int AUTO_LOGIN_EXPIRE_DAY = 30 * 86400;
    /**
     * M站登录用户Id加密key
     **/
    public static final String ENCRYPT_USER_ID_KEY = "6Qz@8%ABPk";
    /**
     * M站登录手机验证码的图片验证码session
     **/
    public static final String PHONE_AUTH_CAPTCHA = "PHONE_AUTH_CAPTCHA";
    /**
     * W站可以重置密码的标志
     **/
    public static final String AUTH_TO_RESETPWD = "AUTH_TO_RESETPWD";
    /**
     * W站图片验证码session
     **/
    public static final String AUTH_CAPTCHA = "AUTH_CAPTCHA";
    /**
     * M站登录手机验证码发送时间session
     **/
    public static final String PHONE_AUTH_CODE_SEND_TIME = "PHONE_AUTH_SEND_TIME";
    /**
     * 手机验证码发送间�?(毫秒)
     **/
    public static final int PHONE_AUTH_SEND_INTERVAL = 60 * 1000;
    /**
     * 密码最大错误次
     **/
    public static final long MAX_PASS_ERROR_COUNT = 5;
    /**
     * 密码错误超限屏蔽登陆时间
     **/
    public static final long BAND_PHONE_TIME = 86400;
    /**
     * 微信open id
     **/
    public static final String WEIXIN_OPEN_ID = "WEIXIN_OPEN_ID";

    /**
     * 微信union id
     **/
    public static final String WEIXIN_UNION_ID = "WEIXIN_UNION_ID";
    /**
     * 微信用户信息
     **/
    public static final String WEIXIN_USER = "WEIXIN_USER";
    /**
     * 微信openId过期时间
     **/
    public static final int WEIXIN_OPENID_EXPIRE_DAY = 100;
    /**
     * M站登录手机验证码记次最大接收次数redis
     **/
    public static final int MAX_PHONE_AUTH_CODE_RECEIVE_COUNT = 3;
    /**
     * M站登录手机验证码记次最大错误次数redis
     **/
    public static final int MAX_PHONE_AUTH_CODE_ERROR_COUNT = 5;
    // /** 用户二维码logo **/
    // public static final String USER_QRCODE_LOGO_URL =
    // "http://static.beta.com/image/favicon/qrcode-130x130.png";
    // /** 用户二维码场景值前缀 **/
    // public static final String USER_QRCODE_SCENE_PREFIX = "user_";

    static {
    }

}
