package cn.cjp.spider.core.discovery;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.RequiredArgsConstructor;
import us.codecraft.webmagic.Page;

/**
 * 通用解析方法
 *
 * 从所有discovery中查找相应的解析方法
 *
 * @author sucre
 */
@RequiredArgsConstructor
public class DiscoveryFactory implements Discovery {

    private final List<Discovery> discoveries = Lists.newArrayList(
        new MatchAHrefInHtmlPagingDiscovery(),
        new PageIndexIncrementPagingDiscovery(),
        new UrlTemplateDiscovery()
    );

    @Override
    public void discover(Page page, SeedDiscoveryRule discovery) {
        int               type          = discovery.getType();
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
