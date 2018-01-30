package cn.cjp.spider.core.config;

import cn.cjp.spider.core.model.PageModel;
import cn.cjp.spider.core.model.ParseRuleModel;
import cn.cjp.spider.util.ValidatorUtil;
import cn.cjp.utils.JacksonUtil;
import cn.cjp.utils.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpiderConfig {

	public static final Logger LOGGER = Logger.getLogger(SpiderConfig.class);

	public static final String SPIDER_RULE_FILE_SUFFIX = ".json";

	/**
	 * 解析规则配置文件的后缀
	 */
	public static final String PARSE_RULE_FILE_SUFFIX = ".parse.json";

	public static final Map<String, PageModel> PAGE_RULES = new HashMap<>();

	public static final Map<String, ParseRuleModel> PARSE_RULES = new HashMap<>();

	static {
		readConfigs();
		combineParseRule();
	}

	/**
	 * 获取当前站点URL的解析规则
	 *
	 * @param site
	 *            站点
	 * @param url
	 *            当前URL
	 * @return
	 */
	public static Optional<ParseRuleModel> getParseRule(String site, String url) {
		final Map<String, PageModel> pageModels = PAGE_RULES;
		final PageModel pageModel = pageModels.get(site);
		final List<ParseRuleModel> parseRuleModels = pageModel.getParseRuleModels();
		return parseRuleModels.stream().filter(parseRuleModel -> {
			return url.matches(parseRuleModel.getUrlPattern());
		}).distinct().findAny();
	}

	/**
	 * 整理每一个站点的parse rule
	 */
	private static void combineParseRule() {
		final Map<String, PageModel> pageModels = PAGE_RULES;
		pageModels.keySet().forEach(site -> {
			PageModel pageModel = PAGE_RULES.get(site);
			List<String> parseRules = pageModel.getParseRules();
			List<ParseRuleModel> parseRuleModels = new ArrayList<>();
			parseRules.forEach(parseRule -> {
				ParseRuleModel parseRuleModel = PARSE_RULES.get(parseRule);
				parseRuleModels.add(parseRuleModel);
			});
			pageModel.setParseRuleModels(parseRuleModels);
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
					ParseRuleModel parseRuleModel = JacksonUtil.fromJsonToObj(jsonStr, ParseRuleModel.class);
					ValidatorUtil.validateWithTemplateByParam(parseRuleModel);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("read parse rule[%s]: %s", parseRuleModel.getRuleName(),
								JacksonUtil.toJson(parseRuleModel)));
					}
					PARSE_RULES.put(parseRuleModel.getRuleName(), parseRuleModel);
				} else if (configFile.getName().endsWith(SPIDER_RULE_FILE_SUFFIX)) {
					PageModel pageModel = JacksonUtil.fromJsonToObj(jsonStr, PageModel.class);
					ValidatorUtil.validateWithTemplateByParam(pageModel);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("read parse rule[%s]: %s", pageModel.getSiteName(),
								JacksonUtil.toJson(pageModel)));
					}
					PAGE_RULES.put(pageModel.getSiteName(), pageModel);
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
