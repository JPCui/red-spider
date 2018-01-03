package cn.cjp.spider.core.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;

import cn.cjp.spider.core.model.PageModel;
import cn.cjp.utils.FileUtil;
import cn.cjp.utils.JacksonUtil;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.scheduler.RedisPriorityScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class SimpleProcessorTest {

    SimpleProcessor simpleProcessor;

    String url;

    public SimpleProcessorTest() throws IOException {
        File file = ResourceUtils.getFile("classpath:spider/module/99lib.json");
        if (!file.exists()) {
            throw new IOException("file not found");
        }
        @SuppressWarnings("unchecked")
        List<String> list = FileUtils.readLines(file);
        String jsonStr = list.stream().filter(s -> {
            return !s.trim().startsWith("//");
        }).reduce((a, b) -> a + b).get();

        JSONObject json = JSONObject.parseObject(jsonStr);
        String value = json.toString();
        PageModel siteModel = JacksonUtil.fromJsonToObj(value, PageModel.class);

        simpleProcessor = new SimpleProcessor();
        simpleProcessor.setPageModel(siteModel);

        url = siteModel.getUrl();

    }

    @Test
    public void testProcessor() {

        JedisPool jedisPool = new JedisPool("localhost");

        Scheduler scheduler = new RedisPriorityScheduler(jedisPool);

        Spider.create(simpleProcessor).setScheduler(scheduler).addPipeline(new ConsolePipeline()).addUrl(url).run();
        // Spider.create(simpleProcessor).addPipeline(new
        // ConsolePipeline()).addUrl(url).run();
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:spider/module/99lib.json");
        System.out.println(file.exists());

    }

}
