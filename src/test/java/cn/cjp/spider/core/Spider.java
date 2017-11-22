package cn.cjp.spider.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cn.cjp.spider.base.enums.ContentType;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

public class Spider {

    public static void main(String[] args) throws IOException {
        int contentType = 10;
        String s = FileUtils.readFileToString(new File("C:/__HOME__/a.txt"));
        Selectable select = null;

        if (ContentType.HTML.equals(ContentType.fromValue(contentType))) {
            Html html = new Html(s);
            select = html.xpath("success");
        } else if (ContentType.JSON.equals(ContentType.fromValue(contentType))) {
            Json json = new Json(s);
            select = json.jsonPath("$.success");
        }

        System.out.println(select.toString());
    }

}
