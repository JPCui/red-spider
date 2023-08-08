package cn.cjp.spider.core;

import cn.hutool.core.io.FileUtil;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import org.springframework.util.ResourceUtils;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 手动测试dom解析，测试文件：test.txt
 *
 * @author sucre
 *
 */
public class NodeParserTest {

	public static void main(String[] args) throws FileNotFoundException {
		String str = FileUtil.readString(ResourceUtils.getFile("classpath:test.txt"), Charset.defaultCharset());
		System.out.println(str);

		String se = "#d div";
//		String r = "<d[^>]*?>[\\s\\S]*?<\\/d>";
		String r = "<d>((?=[\\s\\S])[^<]*)</d>";

		System.out.println("<d>-X-\n</d>".matches(r));

		Html html = new Html(str);
		Selectable sel = html.replace(r, "");
		html = new Html(sel.get());
		System.out.println("a: " + html.css(se, "text").all());
		System.out.println("b: " + html.css(se, "allText").all());
	}

}
