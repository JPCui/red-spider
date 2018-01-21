package cn.cjp.spider.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class _99libUtilTest {

	Html html = null;

	public _99libUtilTest() throws IOException {
		File file = ResourceUtils.getFile("classpath:sections-in-chaptor.txt");
		String domStr = FileUtils.readFileToString(file);
		html = new Html(domStr);
	}

	@Test
	public void extractValidSections() throws IOException {

		String metaContent = html.css("meta[name=client]", "content").get();
		List<String> allStr = html.css("#content div", "text").all();

		_99libUtil.extractValidSections(allStr, metaContent).forEach(System.out::println);
	}

	@Test
	public void test() {
		Selectable selectable = html.css("#content div");
		System.out.println(selectable.css("div", "text").all());
		System.out.println(selectable.css("div", "allText").all());
	}

}
