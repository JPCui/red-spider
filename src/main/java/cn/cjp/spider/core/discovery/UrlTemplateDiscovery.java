package cn.cjp.spider.core.discovery;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;

@RequiredArgsConstructor
public class UrlTemplateDiscovery implements Discovery {

    private final static Pattern regex           = Pattern.compile("\\{(.*?)}");
    private final static String  INIT_PAGE_INDEX = "{INIT_PAGE_INDEX}";

    @Override
    public void discover(Page page, SeedDiscoveryRule discoveryRule) {
        String urlTemplate = discoveryRule.getUrlTemplate();

        // 替换占位符
        Matcher matcher = regex.matcher(urlTemplate);
        while (matcher.find()) {
            // 占位符
            String rep = matcher.group(0);
            // 占位变量
            String      repVar   = matcher.group(1);
            ResultItems items    = page.getResultItems();
            String      repValue = items.get(repVar).toString();

            urlTemplate = urlTemplate.replace(rep, repValue);
        }

        // 设置初始页码: 1
        urlTemplate = urlTemplate.replace(INIT_PAGE_INDEX, "1");

        final String url = urlTemplate;
        page.addTargetRequest(url);
    }

    @Override
    public SeedDiscoveryType getDiscoveryType() {
        return SeedDiscoveryType.URL_TEMPLATE;
    }
}
