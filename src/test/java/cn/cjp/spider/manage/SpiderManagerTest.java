package cn.cjp.spider.manage;

import cn.cjp.spider.SpiderAppApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SpiderAppApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class SpiderManagerTest {

    @Autowired
    SpiderManager spiderManager;

    @Test
    public void runningList() {
        log.debug(spiderManager.runningList().toString());
    }

    @Test
    public void initSpiders() throws InterruptedException {
        spiderManager.initSpiders();
        log.debug("");

        Thread.sleep(10_000L);
    }

    @Test
    public void run() throws InterruptedException {
        spiderManager.run("https://movie.douban.com/subject/25849006/");

        Thread.sleep(10_000L);
    }

}
