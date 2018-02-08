package cn.cjp.app.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * MVC代码配置方法
 * <p>
 * 用于替代springMVC的配置文件
 *
 * @author Jinpeng Cui
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MVCConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(MVCConfig.class);

    @Autowired
    Symphony symphony;

    @Autowired
    FreeMarkerViewResolver freeMarkerViewResolver;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("staticServerPath", symphony.getStaticServerPath());
        map.put("serverPath", symphony.getServerPath());
        freeMarkerViewResolver.setAttributesMap(map);
    }
}
