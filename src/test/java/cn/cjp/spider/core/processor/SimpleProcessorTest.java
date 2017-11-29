package cn.cjp.spider.core.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Set;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.core.model.PageModel;
import cn.cjp.utils.JacksonUtil;
import cn.cjp.utils.PropertiesUtil;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

public class SimpleProcessorTest {

    SimpleProcessor simpleProcessor;

    String url;

    public SimpleProcessorTest() throws IOException {
        InputStream is = PropertiesUtil.getInputStream("/spider/module/proxy.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder jsonBuilder = new StringBuilder();
        String s = null;
        while ((s = reader.readLine()) != null) {
            s = s.trim();
            if (!s.startsWith("//")) {
                jsonBuilder.append(s);
            }
        }

        String jsonStr = jsonBuilder.toString();

        JSONObject json = JSONObject.parseObject(jsonStr);
        Set<String> keySet = json.keySet();
        keySet.forEach(key -> {
            String value = json.getString(key);
            PageModel siteModel = JacksonUtil.fromJsonToObj(value, PageModel.class);

            simpleProcessor = new SimpleProcessor();
            simpleProcessor.setAttrs(siteModel.getAttrs());
            simpleProcessor.setIsList(siteModel.getIsList());
            simpleProcessor.setParentAttr(siteModel.getParentAttr());
            simpleProcessor.setSkip(siteModel.getSkip());
            simpleProcessor.setFindSeedPattern(siteModel.getFindSeedPattern());

            url = siteModel.getUrl();
        });

    }

    @Test
    public void testProcessor() {
        Spider.create(simpleProcessor).addPipeline(new ConsolePipeline()).addUrl(url).run();
    }

    @Test
    public void test() {
        String p = "http://www.xicidaili.com/nn/(.*)";
        String s = "http://www.xicidaili.com/nn/1";

        System.out.println(s.matches(p));
    }

}
