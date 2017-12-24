package cn.cjp.spider.core.discovery;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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

		List<String> foundUrlList = new ArrayList<>();

		List<String> allUrls = page.getHtml().$("a", "href").all();
		allUrls.forEach(url -> {
			String currUrl = url;
			if (currUrl.startsWith("/")) {
				// 相对路径的处理
				try {
					currUrl = URLUtil.relative(page.getUrl().get(), url);
				} catch (MalformedURLException e) {
					LOGGER.warn(e.getMessage());
				}
			}
			if (currUrl.matches(findSeedPattern)) {
				foundUrlList.add(currUrl);
			}
		});

		page.addTargetRequests(foundUrlList);

	}

	@Override
	public SeedDiscoveryType getDiscoveryType() {
		return SeedDiscoveryType.HTML_NORMAL_PAGING;
	}

}
