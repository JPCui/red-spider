package cn.cjp.spider.core.discovery;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.utils.Logger;
import cn.cjp.utils.StringUtil;
import cn.cjp.utils.URLUtil;
import com.google.common.collect.Lists;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import us.codecraft.webmagic.Page;

/**
 * 以一般分页URL的方式解析
 *
 * @author sucre
 *
 * FIXME 完善注释
 */
public class MatchAHrefInHtmlPagingDiscovery implements Discovery {

    private static final Logger LOGGER = Logger.getLogger(MatchAHrefInHtmlPagingDiscovery.class);

    @Override
    public void discover(Page page, SeedDiscoveryRule discovery) {
        final String findSeedPattern = discovery.getPattern();

        if (StringUtil.isEmpty(findSeedPattern)) {
            return;
        }

        Set<String> foundUrlList = new TreeSet<>();

        List<String> allUrls = page.getHtml().$("a", "href").all();
        allUrls.stream().filter(s -> !StringUtil.isEmpty(s) && !s.startsWith("#")).forEach(url -> {
            String currUrl = url;
            if (!(currUrl.startsWith("http:") || currUrl.startsWith("https:"))) {
                // 相对路径的处理
                try {
                    currUrl = URLUtil.relative(page.getUrl().get(), currUrl);
                } catch (MalformedURLException e) {
                } catch (Exception e) {
                    LOGGER.error(String.format("parse url error, url: %s", url), e);
                }
            }
            if (currUrl.matches(findSeedPattern)) {
                try {
                    // FIXME 99lib定制化逻辑
                    URI.create(currUrl);
                    if (currUrl.matches(".*九.九.藏.书.*")) {
                        LOGGER.error(String.format("illigle argument url : , from : ", url, page.getUrl().get()));
                        LOGGER.error(page.toString());
                    }
                    foundUrlList.add(currUrl);
                } catch (Exception e) {
                    LOGGER.error(String.format("illigle argument url : , from : ", url, page.getUrl().get()));
                    LOGGER.error(page.toString());
                }
            }
        });

        LOGGER.info(String.format("found new urls : ", foundUrlList));
        page.addTargetRequests(Lists.newArrayList(foundUrlList));

    }

    @Override
    public SeedDiscoveryType getDiscoveryType() {
        return SeedDiscoveryType.PAGING_STRATEGY_MATCH_A_HREF_IN_HTML;
    }

}
