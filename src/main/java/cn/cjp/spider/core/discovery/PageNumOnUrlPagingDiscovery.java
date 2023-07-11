package cn.cjp.spider.core.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.utils.StringUtil;
import us.codecraft.webmagic.Page;

/**
 * 通常类似json接口的格式，在url里匹配页码的部分
 *
 * @author sucre
 *
 */
public class PageNumOnUrlPagingDiscovery implements Discovery {

    @Override
    public void discover(Page page, SeedDiscoveryRule discovery) {
        final String findSeedPattern = discovery.getPattern();

        if (StringUtil.isEmpty(findSeedPattern)) {
            return;
        }

        List<String> foundUrlList = new ArrayList<>();

        Pattern pattern = Pattern.compile(findSeedPattern);
        Matcher matcher = pattern.matcher(page.getUrl().get());
        if (matcher.find()) {
            String pageNumStr = matcher.group(2);
            if (pageNumStr != null) {
                int pageNum = Integer.parseInt(pageNumStr);
                // 匹配出页码，在原来的基础上+1
                String foundUrl = matcher.replaceFirst(Integer.toString(pageNum + 1));
                foundUrlList.add(foundUrl);
            }
        }

        page.addTargetRequests(foundUrlList);
    }

    @Override
    public SeedDiscoveryType getDiscoveryType() {
        return SeedDiscoveryType.PAGING_STRATEGY_PAGE_NUM_ON_URL;
    }

}
