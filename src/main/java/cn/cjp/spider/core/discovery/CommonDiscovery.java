package cn.cjp.spider.core.discovery;

import java.util.HashSet;
import java.util.Set;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import us.codecraft.webmagic.Page;

/**
 * 通用解析方法
 *
 * 从所有discovery中查找相应的解析方法
 *
 * @author sucre
 *
 */
public class CommonDiscovery implements Discovery {

    public static final Set<Discovery> discoveries = new HashSet<Discovery>() {
        private static final long serialVersionUID = 2863967778498649876L;
        {
            add(new MatchAHrefInHtmlPagingDiscovery());
            add(new PageNumOnUrlPagingDiscovery());
        }
    };

    @Override
    public void discover(Page page, SeedDiscoveryRule discovery) {
        int type = discovery.getType();
        SeedDiscoveryType discoveryType = SeedDiscoveryType.fromValue(type);

        // 用合适的Discovery解析子页面
        for (Discovery disc : discoveries) {
            if (disc.getDiscoveryType().equals(discoveryType)) {
                disc.discover(page, discovery);
                break;
            }
        }
    }

    @Override
    public SeedDiscoveryType getDiscoveryType() {
        return SeedDiscoveryType.COMMON;
    }

}
