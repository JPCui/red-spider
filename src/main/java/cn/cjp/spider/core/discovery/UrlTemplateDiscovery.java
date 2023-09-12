package cn.cjp.spider.core.discovery;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;

@RequiredArgsConstructor
@Slf4j
public class UrlTemplateDiscovery implements Discovery {

    private final static Pattern regex           = Pattern.compile("\\{(.*?)}");
    private final static String  INIT_PAGE_INDEX = "{INIT_PAGE_INDEX}";

    @Override
    public void discover(Page page, SeedDiscoveryRule discoveryRule) {
        String urlTemplate = discoveryRule.getUrlTemplate();

        ResultItems items = page.getResultItems();
        if (items.get("jsons") != null) {
            List<JSONObject> jsons = items.get("jsons");
            jsons.forEach(json -> {
                page.addTargetRequest(parseNewUrl(urlTemplate, json));
            });
        } else if (items.get("json") != null) {
            JSONObject json = items.get("json");
            page.addTargetRequest(parseNewUrl(urlTemplate, json));
        } else {
            log.error("没有合适的参数");
        }

    }

    private String parseNewUrl(String urlTemplate, JSONObject item) {
        // 替换占位符
        Matcher matcher = regex.matcher(urlTemplate);
        if (matcher.find()) {
            // 占位符
            String rep = matcher.group(0);
            // 占位变量
            String repVar = matcher.group(1);

            String repValue = item.get(repVar).toString();

            urlTemplate = urlTemplate.replace(rep, repValue);
        }

        // 设置初始页码: 1
        urlTemplate = urlTemplate.replace(INIT_PAGE_INDEX, "1");

        final String url = urlTemplate;
        log.info(String.format("found new urls: %s", url));
        return url;
    }

    @Override
    public SeedDiscoveryType getDiscoveryType() {
        return SeedDiscoveryType.URL_TEMPLATE;
    }
}
