package cn.cjp.spider.core.discovery;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import us.codecraft.webmagic.Page;

public interface Discovery {

    public void discover(Page page, SeedDiscoveryRule discovery);
    
    public SeedDiscoveryType getDiscoveryType();

}
