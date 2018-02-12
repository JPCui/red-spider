package cn.cjp.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.cjp.core.cache.Cache;
import cn.cjp.core.cache.CacheInterceptor;
import cn.cjp.core.cache.CacheManager;
import cn.cjp.core.cache.file.FileCache;

@Configuration
public class CacheConfig {

    @Bean
    public Cache cache() {
        FileCache fileCache = new FileCache();
        return fileCache;
    }

    @Bean
    public CacheManager cacheManager(@Autowired Cache cache) {

        CacheManager cacheManager = new CacheManager(cache);
        return cacheManager;
    }

    @Bean
    public CacheInterceptor cacheInterceptor(@Autowired CacheManager cacheManager) {
        return new CacheInterceptor(cacheManager);
    }

}
