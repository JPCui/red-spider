package cn.cjp.spider.core.config;

import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.model.ParseRuleModel;
import cn.cjp.spider.util.ValidatorUtil;
import cn.cjp.utils.Logger;
import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

public class SpiderConfig {

    public static final Logger LOGGER = Logger.getLogger(SpiderConfig.class);

    public static final String SPIDER_RULE_FILE_SUFFIX = ".json";

    /**
     * 解析规则配置文件的后缀
     */
    public static final String PARSE_RULE_FILE_SUFFIX = ".parse.json";

    public static final Map<String, SiteModel> PAGE_RULES = new HashMap<>();

    public static final Map<String, ParseRuleModel> PARSE_RULES = new HashMap<>();

    static {
        readConfigs();
        combineParseRule();
    }

    /**
     * 获取当前站点URL的解析规则
     *
     * @param site 站点
     * @param url 当前URL
     */
    public static Optional<ParseRuleModel> getParseRule(String site, String url) {
        final Map<String, SiteModel> pageModels      = PAGE_RULES;
        final SiteModel              siteModel       = pageModels.get(site);
        final List<ParseRuleModel>   parseRuleModels = siteModel.getParseRuleModels();
        return parseRuleModels.stream().filter(parseRuleModel -> {
            return url.matches(parseRuleModel.getUrlPattern());
        }).distinct().findAny();
    }

    /**
     * 整理每一个站点的parse rule
     */
    private static void combineParseRule() {
        final Map<String, SiteModel> pageModels = PAGE_RULES;
        pageModels.keySet().forEach(site -> {
            SiteModel            siteModel       = PAGE_RULES.get(site);
            List<String>         parseRules      = siteModel.getParseRules();
            List<ParseRuleModel> parseRuleModels = new ArrayList<>();
            parseRules.forEach(parseRule -> {
                ParseRuleModel parseRuleModel = PARSE_RULES.get(parseRule);
                parseRuleModels.add(parseRuleModel);
            });
            siteModel.setParseRuleModels(parseRuleModels);
        });
    }

    private static void readConfigs() {
        try {
            File configPath = ResourceUtils.getFile("classpath:spider/module");

            if (!configPath.exists()) {
                throw new IOException("file not found");
            }
            File[] configFiles = configPath.listFiles();

            for (File configFile : configFiles) {
                if (ignore(configFile)) {
                    continue;
                }
                List<String> list = FileUtils.readLines(configFile);
                String jsonStr = list.stream().filter(s -> {
                    // 过滤注释语句
                    return !s.trim().startsWith("//");
                }).reduce((a, b) -> a.concat(b)).get();
                if (configFile.getName().endsWith(PARSE_RULE_FILE_SUFFIX)) {
                    ParseRuleModel parseRuleModel = JSON.parseObject(jsonStr, ParseRuleModel.class);
                    ValidatorUtil.validateWithTemplateByParam(parseRuleModel);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(String.format("read parse rule[%s]: %s", parseRuleModel.getRuleName(),
                                                   JSON.toJSONString(parseRuleModel)));
                    }
                    PARSE_RULES.put(parseRuleModel.getRuleName(), parseRuleModel);
                } else if (configFile.getName().endsWith(SPIDER_RULE_FILE_SUFFIX)) {
                    SiteModel siteModel = JSON.parseObject(jsonStr, SiteModel.class);
                    ValidatorUtil.validateWithTemplateByParam(siteModel);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(String.format("read parse rule[%s]: %s", siteModel.getSiteName(),
                                                   JSON.toJSONString(siteModel)));
                    }
                    PAGE_RULES.put(siteModel.getSiteName(), siteModel);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static boolean ignore(File file) {
        List<String> ignoreFiles = new ArrayList<>();
        ignoreFiles.add("blog.csdn.com.json");
        ignoreFiles.add("demo.json");
        ignoreFiles.add("proxy.json");

        return ignoreFiles.contains(file.getName());
    }

}
