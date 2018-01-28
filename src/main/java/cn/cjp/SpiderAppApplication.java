package cn.cjp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@SpringBootApplication
@ServletComponentScan
public class SpiderAppApplication extends SpringBootServletInitializer {

	/**
	 * 创建异步任务执行器
	 * 
	 * @return
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

	public static void main(String[] args) {
		SpringApplication.run(SpiderAppApplication.class, args);
	}

}
