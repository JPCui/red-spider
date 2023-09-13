package cn.cjp.spider.core.config;

import cn.cjp.spider.core.model.CredentialDomain;
import cn.cjp.spider.core.model.ParseRuleModel;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.util.ValidatorUtil;
import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StreamUtils;

@Slf4j
public class SpiderConfig {

    public static final String SPIDER_RULE_FILE_SUFFIX = ".json";

    /**
     * 解析规则配置文件的后缀
     */
    public static final String PARSE_RULE_FILE_SUFFIX = ".parse.json";

    /**
     * @deprecated 转到数据库中
     */
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
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:spider/module/*");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                log.info("file: " + filename);
                if(ignore(filename)) {
                    continue;
                }
                String jsonStr = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
                if (filename.endsWith(PARSE_RULE_FILE_SUFFIX)) {
                    ParseRuleModel parseRuleModel = JSON.parseObject(jsonStr, ParseRuleModel.class);
                    ValidatorUtil.validateWithTemplateByParam(parseRuleModel);
                    log.debug(String.format("read parse rule[%s]: %s", parseRuleModel.getRuleName(),
                                            JSON.toJSONString(parseRuleModel)));
                    PARSE_RULES.put(parseRuleModel.getRuleName(), parseRuleModel);
                } else if (filename.endsWith(SPIDER_RULE_FILE_SUFFIX)) {
                    SiteModel siteModel = JSON.parseObject(jsonStr, SiteModel.class);
                    ValidatorUtil.validateWithTemplateByParam(siteModel);
                    log.debug(String.format("read parse rule[%s]: %s", siteModel.getSiteName(),
                                            JSON.toJSONString(siteModel)));
                    PAGE_RULES.put(siteModel.getSiteName(), siteModel);
                }


            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static boolean ignore(String file) {
        List<String> ignoreFiles = new ArrayList<>();
        ignoreFiles.add("blog.csdn.com.json");
        ignoreFiles.add("demo.json");
        ignoreFiles.add("proxy.json");

        return ignoreFiles.stream().anyMatch(ignoreFile -> {
            if (ignoreFile.equalsIgnoreCase(file)) {
                return true;
            }
            return false;
        });
    }

    /**
     * @deprecated 从数据库读取
     */
    public static CredentialDomain mockEmailCredential() {
        CredentialDomain domain = new CredentialDomain();
        domain.setId("pipeline_email_1");
        domain.setHost("smtp.qq.com");
        domain.setPort(587);
        domain.setAuth(true);
        domain.setFrom("624498030@qq.com");
        domain.setUser("624498030@qq.com");
        domain.setPass("cogqsnusvkgebbbc");
        domain.setSslEnable(false);
        return domain;
    }
}
