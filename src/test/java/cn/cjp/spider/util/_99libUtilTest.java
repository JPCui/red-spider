package cn.cjp.spider.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import us.codecraft.webmagic.selector.Html;

public class _99libUtilTest {

	@Test
	public void t() throws IOException {
		File file = ResourceUtils.getFile("classpath:sections-in-chaptor.txt");
		String domStr = FileUtils.readFileToString(file);

		Html html = new Html(domStr);
		String metaContent = html.css("meta[name=client]", "content").get();
		List<String> allStr = html.css("#content div", "text").all();

		_99libUtil.extractValidSections(allStr, metaContent).forEach(System.out::println);

	}

}
