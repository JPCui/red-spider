package cn.cjp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.springframework.util.ResourceUtils;
import us.codecraft.webmagic.selector.Html;

public class StringTest {

    @Test
    public void testMatch() throws IOException {
        String str = FileUtils.readFileToString(ResourceUtils.getFile("classpath:tmp.html"));

        Document doc = Html.create(str).getDocument();

        Iterator<Element> it = doc.select(".css-1fjsk39-linkText").iterator();

        Set<String> urls = new TreeSet<>();

        while (it.hasNext()) {
            String s = it.next().text().trim();
            urls.add(s);
        }
        urls.forEach(s->{
            String tpl = "<url>\n"
                + "        <loc>{loc}</loc>\n"
                + "        <lastmod>2021-11-01</lastmod>\n"
                + "    </url>";
            if (s.startsWith("https")) {
                System.out.println(tpl.replace("{loc}", s.trim()));
            }
        });


    }

}
