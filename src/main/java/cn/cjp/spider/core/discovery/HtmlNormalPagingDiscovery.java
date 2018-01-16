package cn.cjp.spider.core.discovery;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import cn.cjp.spider.core.enums.SeedDiscoveryType;
import cn.cjp.spider.core.model.SeedDiscoveryRule;
import cn.cjp.utils.Logger;
import cn.cjp.utils.StringUtil;
import cn.cjp.utils.URLUtil;
import us.codecraft.webmagic.Page;

/**
 * 以一般分页URL的方式解析
 * 
 * @author sucre
 *
 */
public class HtmlNormalPagingDiscovery implements Discovery {

	private static final Logger LOGGER = Logger.getLogger(HtmlNormalPagingDiscovery.class);

	@Override
	public void discover(Page page, SeedDiscoveryRule discovery) {
		final String findSeedPattern = discovery.getPattern();

		if (StringUtil.isEmpty(findSeedPattern)) {
			return;
		}

		Set<String> foundUrlList = new TreeSet<>();

		List<String> allUrls = page.getHtml().$("a", "href").all();
		allUrls.forEach(url -> {
			String currUrl = url;
			if (!(currUrl.startsWith("http:") || currUrl.startsWith("https:"))) {
				// 相对路径的处理
				try {
					currUrl = URLUtil.relative(page.getUrl().get(), currUrl);
				} catch (MalformedURLException e) {
					LOGGER.warn(e.getMessage());
				}
			}
			if (currUrl.matches(findSeedPattern)) {
				foundUrlList.add(currUrl);
			}
		});

		LOGGER.info(String.format("found new urls : ", foundUrlList));
		page.addTargetRequests(Lists.newArrayList(foundUrlList));

	}

	@Override
	public SeedDiscoveryType getDiscoveryType() {
		return SeedDiscoveryType.HTML_NORMAL_PAGING;
	}

}
