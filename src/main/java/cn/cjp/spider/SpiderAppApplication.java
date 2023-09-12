package cn.cjp.spider;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = "cn.cjp")
@EnableSwagger2
@EnableScheduling
public class SpiderAppApplication {

//    static {
//        ParserConfig.getGlobalInstance().addAccept("cn.cjp.spider.core.model.");
//        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//    }

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
        simpleAsyncTaskExecutor.setConcurrencyLimit(Runtime.getRuntime().availableProcessors() * 2);
        return simpleAsyncTaskExecutor;
    }

}
