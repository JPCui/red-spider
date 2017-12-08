package cn.cjp.spider.core.processor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Test;

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
        String dir = "./spider/module";
        File path = new File(dir);
        File[] files = path.listFiles();
        List<String> list = FileUtil.read(files[0]);
        String jsonStr = list.stream().filter(s -> {
            return !s.trim().startsWith("//");
        }).reduce((a, b) -> a + b).get();

        JSONObject json = JSONObject.parseObject(jsonStr);
        Set<String> keySet = json.keySet();
        keySet.forEach(key -> {
            String value = json.getString(key);
            PageModel siteModel = JacksonUtil.fromJsonToObj(value, PageModel.class);

            simpleProcessor = new SimpleProcessor();
            simpleProcessor.setPageModel(siteModel);

            url = siteModel.getUrl();
        });

    }

    @Test
    public void testProcessor() {

        JedisPool jedisPool = new JedisPool("localhost");

        Scheduler scheduler = new RedisPriorityScheduler(jedisPool);

        Spider.create(simpleProcessor).setScheduler(scheduler).addPipeline(new ConsolePipeline()).addUrl(url).run();
        // Spider.create(simpleProcessor).addPipeline(new
        // ConsolePipeline()).addUrl(url).run();
    }

    public static void main(String[] args) {
        File file = new File("./spider/module/blog.csdn.com.json");

        System.out.println(file.getAbsolutePath());

    }

}
