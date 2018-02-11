package cn.cjp.wechat;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    private String appId;

    private String appSecret;

    private Valid valid;

    private Msg msg;

    @Data
    public static class Valid {

        private String token;

        private String encodingAESKey;

    }

    /**
     * 消息模块
     */
    @Data
    public static class Msg {

        /**
         * 默认回答
         */
        private String dft = "On my way ~";

        private String onSubscribe = "谢谢关注 ~";

        private String unsupport = "暂不支持哦 ~";

    }


}
