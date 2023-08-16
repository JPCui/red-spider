package cn.cjp.spider.core.spider.listener;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

@Slf4j
public class DefaultMonitorListener implements SpiderListener {

    private final static DefaultMonitorListener monitorListener = new DefaultMonitorListener();

    private List<Spider> spiders = new ArrayList<>();

    public static void regist(Spider... spiders) {
        monitorListener.spiders.addAll(Arrays.asList(spiders));

        for (Spider spider : spiders) {
            spider.setSpiderListeners(Lists.newArrayList(monitorListener));
        }
    }

    @Override
    public void onSuccess(Request request) {
        spiders.forEach(spider -> {
            String    domain    = spider.getSite().getDomain();
            Scheduler scheduler = spider.getScheduler();
            if (scheduler instanceof MonitorableScheduler) {
                MonitorableScheduler monitorableScheduler = (MonitorableScheduler) scheduler;
                int                  leftRequestsCount    = monitorableScheduler.getLeftRequestsCount(spider);
                int                  totalRequestsCount   = monitorableScheduler.getTotalRequestsCount(spider);

                log.info(String.format("%s, total: %s, left: %s", domain, totalRequestsCount, leftRequestsCount));
            }

        });
    }

    @Override
    public void onError(Request request) {

    }
}
