package cn.cjp.app.config;

import com.google.common.collect.Lists;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;
import java.util.Map;

/**
 * MVC代码配置方法
 * <p>
 * 用于替代springMVC的配置文件
 *
 * @author Jinpeng Cui
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MVCConfig extends WebMvcConfigurerAdapter {

    private static final Logger logger = Logger.getLogger(MVCConfig.class);

    @Autowired
    private Symphony symphony;

    @Autowired
    protected FreeMarkerProperties freeMarkerProperties;

    @Bean
    public ContentNegotiatingViewResolver viewResolver() {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setDefaultViews(Lists.newArrayList(jsonView()));
        viewResolver.setViewResolvers(Lists.newArrayList(freeMarkerViewResolver()));
        return viewResolver;
    }

    public MappingJackson2JsonView jsonView() {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        return jsonView;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.freemarker.enabled", matchIfMissing = true)
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();

        Map<String, Object> map = new HashMap<>();
        map.put("staticServerPath", symphony.getStaticServerPath());
        map.put("serverPath", symphony.getServerPath());
        resolver.setAttributesMap(map);

        this.freeMarkerProperties.applyToViewResolver(resolver);
        return resolver;
    }
}
