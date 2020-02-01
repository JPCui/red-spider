package cn.cjp.spider.core.spider;

import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

/**
 * scheduler should implements {@link MyRedisScheduler}
 *
 * @author sucre
 */
@Slf4j
public class MyRedisSchedulerSpider extends AbstractSpider {

    public MyRedisSchedulerSpider(PageProcessor pageProcessor, MyRedisScheduler scheduler) {
        super(pageProcessor, scheduler);
    }

    protected void onSuccess(Request request) {
        super.onSuccess(request);
        ((MyRedisScheduler) this.scheduler).onDownloadSuccess(request, this);
    }

}
