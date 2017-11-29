package cn.cjp.spider.core;

import us.codecraft.webmagic.selector.Html;

public class Demo {

    public static void main(String[] args) {
        Html html = new Html("{\"a\":\"1\"}");
        System.out.println(html.jsonPath("a"));
    }

}
