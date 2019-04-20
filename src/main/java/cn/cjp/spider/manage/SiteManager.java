package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.discovery.CommonDiscovery;
import cn.cjp.spider.core.discovery.Discovery;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.utils.URLUtil;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SiteManager {

    Discovery discovery = new CommonDiscovery();

    public Optional<SiteModel> getSiteModel(String url) {
        return SpiderConfig.PAGE_RULES.values().stream().filter(siteModel -> {
            String domain = siteModel.getDomain();
            return URLUtil.getHost(url).endsWith(domain);
        }).findFirst();

    }


}
