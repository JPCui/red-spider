package cn.cjp.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "symphony", ignoreUnknownFields = true)
public class Symphony {

    private boolean enable;

    private String staticServerPath;

    private String serverPath;

    private Login login;

    @Data
    public static class Login {

        private String secret;

    }

}
