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

        String se = "div.intro";

        System.out.println();

        Html html = new Html(str);
        System.out.println("a: " + html.css(se).all());
        System.out.println("b: " + html.css(se, "allText").all());
    }

}
