package cn.cjp.spider.core.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindSeedProcessorTest {

    public static void main(String[] args) {
        String url = "http://abc.com/?xxx=&pageSize=10&pageNum=2";

        Pattern pattern = Pattern.compile("http://abc.com/\\?xxx=&pageSize=(.*)&pageNum=(.*)");
        Matcher matcher = pattern.matcher(url);

        while (matcher.find()) {
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }

    }

}
