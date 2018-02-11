package cn.cjp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import cn.cjp.app.config.Symphony;
import cn.cjp.wechat.WechatProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@ComponentScan({"cn.cjp"})
@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties({Symphony.class, WechatProperties.class})
@EnableSwagger2
public class SpiderAppApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpiderAppApplication.class, args);
    }

    /**
     * 创建异步任务执行器
     */
    @Bean
    public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setThreadNamePrefix("Async-Task-");
        simpleAsyncTaskExecutor.setDaemon(true);
        simpleAsyncTaskExecutor.setConcurrencyLimit(10);
        return simpleAsyncTaskExecutor;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpiderAppApplication.class);
    }

}
