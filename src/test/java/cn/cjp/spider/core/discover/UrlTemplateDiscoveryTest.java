package cn.cjp.spider.core.discover;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

public class UrlTemplateDiscoveryTest {

    @Test
    public void testReplaceTemplate() {

        String urlTemplate = "http://example.com/?a={a}&b={b}";

        Pattern regex   = Pattern.compile("\\{(.*?)}");
        Matcher matcher = regex.matcher(urlTemplate);

        matcher.find();
        Assert.assertEquals(matcher.group(0), "{a}");
        Assert.assertEquals(matcher.group(1), "a");

        matcher.find();
        Assert.assertEquals(matcher.group(0), "{b}");
        Assert.assertEquals(matcher.group(1), "b");
    }

}
