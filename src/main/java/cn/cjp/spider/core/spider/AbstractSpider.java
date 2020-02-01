package cn.cjp.spider.core.spider;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

@Slf4j
public abstract class AbstractSpider extends us.codecraft.webmagic.Spider {

    /**
     *
     */
    public AbstractSpider(PageProcessor pageProcessor, Scheduler scheduler) {
        super(pageProcessor);
        super.setScheduler(scheduler);
    }

    /**
     * @deprecated don`t use it
     */
    public Spider setScheduler(Scheduler scheduler) {
        log.error("使用构造函数设置 scheduler");
        return null;
    }


}
