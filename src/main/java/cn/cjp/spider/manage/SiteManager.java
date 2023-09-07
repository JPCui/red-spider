package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.model.SiteModel;
import cn.hutool.core.util.URLUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SiteManager {

    public Optional<SiteModel> getSiteModel(String url) {
        return SpiderConfig.PAGE_RULES.values().stream().filter(siteModel -> {
            String domain = siteModel.getDomain();
            try {
                return URLUtil.getHost(new URL(url)).getHost().endsWith(domain);
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }).findFirst();

    }


}
