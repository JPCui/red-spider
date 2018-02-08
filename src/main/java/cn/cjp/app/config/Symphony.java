package cn.cjp.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "symphony", ignoreUnknownFields = true)
@ConditionalOnProperty("symphony.enable")
public class Symphony {

    private boolean enable;

    private String staticServerPath;

    private String serverPath;

}
