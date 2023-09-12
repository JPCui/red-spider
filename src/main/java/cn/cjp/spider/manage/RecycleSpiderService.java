package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * 循环任务Service，定时【重置】抓取队列即可
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@RequiredArgsConstructor
public class RecycleSpiderService {

    final SpiderMonitor spiderMonitor;
    final Scheduler     scheduler;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void run() {
        log.info("定时清理循环任务.");
        SpiderConfig.PAGE_RULES.forEach((siteName, siteModel) -> {
            if (siteModel.isRecycle()) {
                Spider spider = spiderMonitor.get(siteModel);
                if (spider != null) {
                    ((MyRedisScheduler) scheduler).clearAllQueue(spider);
                    spider.addUrl(siteModel.getUrl());
                    log.info(spider.getUUID() + " clear");
                }
            }
        });
    }

}
