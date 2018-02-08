package cn.cjp.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.cjp.core.cache.CacheInterceptor;
import cn.cjp.core.cache.CacheManager;
import cn.cjp.core.cache.file.FileCache;

@Configuration
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		FileCache fileCache = new FileCache();

		CacheManager cacheManager = new CacheManager(fileCache);
		return cacheManager;
	}

	@Bean
	public CacheInterceptor cacheInterceptor(@Autowired CacheManager cacheManager) {
		return new CacheInterceptor(cacheManager);
	}

}