package cn.cjp.spider.core;

import java.io.FileNotFoundException;

import org.springframework.util.ResourceUtils;

import cn.cjp.utils.FileUtil;
import cn.cjp.utils.StringUtil;
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
		String str = StringUtil.combine(FileUtil.read(ResourceUtils.getFile("classpath:test.txt")), "");
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
