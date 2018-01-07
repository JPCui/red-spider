package cn.cjp.spider.core;

import java.io.FileNotFoundException;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.ResourceUtils;

import cn.cjp.utils.FileUtil;
import cn.cjp.utils.StringUtil;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.Html;

public class NodeParserTest {

	public static void main(String[] args) throws FileNotFoundException {
		String str = StringUtil.combine(FileUtil.read(ResourceUtils.getFile("classpath:test.txt")), "");
		System.out.println(str);

		String se = "h4:eq(2)";

		System.out.println();

		Document doc = new Document("http://baidu.com");
		doc.append(str);
		Elements elems = doc.select(se);
		System.out.println(elems);
		System.out.println(elems.text());

		System.out.println();

		Html html = new Html(str);
		System.out.println(html.css(se).all());
		System.out.println(html.css(se, "allText").all());
		System.out.println(html.css(se));
		System.out.println(html.css(se, "allText"));

		CssSelector cssSelector = new CssSelector(se, "text");
		System.out.println();
		System.out.println(html.select(cssSelector));
		System.out.println(html.selectDocument(cssSelector));
		System.out.println(html.selectList(cssSelector).all());
		System.out.println(html.selectDocumentForList(cssSelector));
	}

}
