package cn.cjp.spider.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cn.cjp.spider.core.enums.ParserType;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

public class Spider {

    public static void main(String[] args) throws IOException {
        ParserType parserType = ParserType.REGEX;
        String s = FileUtils.readFileToString(new File("C:/__HOME__/b.txt"));
        Selectable select = null;

        if (ParserType.HTML.equals(parserType)) {
            Html html = new Html(s);
            select = html.$("h1");
        } else if (ParserType.JSON.equals(parserType)) {
            Json json = new Json(s);
            select = json.jsonPath("$.success");
        } else if (ParserType.REGEX.equals(parserType)) {
            PlainText plainText = new PlainText(s);
            select = plainText.regex("<h1 class=\\\"hello.*\\\">(.*)</h1>");
        }

        System.out.println(select.all());
    }

}
