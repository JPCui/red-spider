package cn.cjp.spider.core.discover;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

public class PageIndexIncrementDiscoveryTest {

    @Test
    public void testPageIndexIncr() {
        String currUrl = "http://example.com?a=1&pageIndex=99&b=2";
        String tmplUrl = "pageIndex=([0-9]*)";

        Pattern compile = Pattern.compile(tmplUrl);
        Matcher matcher = compile.matcher(currUrl);

        Assert.assertTrue(matcher.find());
        Assert.assertEquals(matcher.group(0), "pageIndex=99");
        Assert.assertEquals(matcher.group(1), "99");
    }

}
