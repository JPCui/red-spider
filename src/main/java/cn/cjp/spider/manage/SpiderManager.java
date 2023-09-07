package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.pipeline.SimplePipeline;
import cn.cjp.spider.core.processor.html.SimpleProcessor;
import cn.cjp.spider.core.spider.listener.DefaultMonitorListener;
import cn.cjp.spider.dto.ProcessorProperties;
import cn.cjp.spider.exception.ServiceException;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@RequiredArgsConstructor
public class SpiderManager {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    final ExecutorService executorService = Executors.newCachedThreadPool();

    final Map<String, Spider> runningSpiders        = new HashMap<>();
    /**
     * 正在运行的循环任务spider
     */
    final Map<String, Spider> runningRecycleSpiders = new HashMap<>();

    final Scheduler scheduler;

    final SiteManager siteManager;

    public Collection<String> runningList() {
        return Collections.unmodifiableCollection(runningSpiders.keySet());
    }

    public void startAll() {
        ProcessorProperties props = new ProcessorProperties();

        SpiderConfig.PAGE_RULES.forEach((siteName, siteModel) -> {
            if (!_isRunning(siteModel)) {
                start(siteName);
                log.info(String.format("spider[%s] start.", siteName));
            }
            log.info(String.format("spider[%s] is already start.", siteName));
        });
    }

    public void start(String siteName) {
        try {
            this.lock.writeLock().lock();
            SiteModel siteModel = SpiderConfig.PAGE_RULES.get(siteName);
            if (!_isRunning(siteModel)) {
                Spider spider = initProcessor(siteModel, new ProcessorProperties());
                _push(siteModel, spider);
                spider.start();
                log.info(String.format("spider[%s] start.", siteName));
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private boolean _isRunning(SiteModel siteModel) {
        return this.runningSpiders.containsKey(siteModel.getSiteName());
    }

    private void _push(SiteModel siteModel, Spider spider) {
        runningSpiders.put(siteModel.getSiteName(), spider);
        if (siteModel.isRecycle()) {
            runningRecycleSpiders.put(siteModel.getSiteName(), spider);
        }
    }

    private Spider _pop(SiteModel siteModel) {
        String siteName      = siteModel.getSiteName();
        Spider spider        = runningSpiders.remove(siteName);
        Spider recycleSpider = runningRecycleSpiders.remove(siteName);

        return spider == null ? recycleSpider : spider;
    }

    public void stop(String siteName) {
        try {
            this.lock.writeLock().lock();
            SiteModel siteModel = SpiderConfig.PAGE_RULES.get(siteName);
            Optional.ofNullable(_pop(siteModel)).ifPresent(Spider::stop);
            log.info(String.format("spider[%s] stop.", siteName));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * FIXME 1 如果配置在下次运行时才生效？需要先停止，然后修改配置信息，最后重新运行
     *
     * TODO 2 当前修改配置是临时保存在内存里，需要切换为数据库操作
     */
    public void config(String siteName, ProcessorProperties props) {
        if (runningSpiders.containsKey(siteName)) {
            Spider spider = runningSpiders.get(siteName);
            Site   site   = spider.getSite();
            site.setSleepTime(props.getSleepTime()); // 每次抓取完畢，休息
            site.setRetrySleepTime(props.getRetrySleepTime()); // 重试休息时间：30s
            site.setRetryTimes(props.getRetryTimes()); // 重试 10次
            site.setTimeOut(props.getTimeout()); // 超时时间 30s
        }
    }

    private Spider initProcessor(SiteModel siteModel, ProcessorProperties props) {
        Spider spider = buildSpider(siteModel, props);

        return spider;
    }

    public void run(String url) {
        Optional<SiteModel> siteModelOptional = siteManager.getSiteModel(url);

        siteModelOptional.ifPresent(siteModel -> {
            Spider spider = buildSpider(siteModel, new ProcessorProperties());
            spider.setExitWhenComplete(true);
            if (siteModel.isAutoStartup()) {
                spider.run();
                log.error(spider.getSite().getDomain() + " is ready and running.");
            } else {
                log.error(spider.getSite().getDomain() + " is ready.");
            }
        });

        if (!siteModelOptional.isPresent()) {
            throw new ServiceException("无对应的爬取模板");
        }
    }

    private Spider buildSpider(SiteModel siteModel, ProcessorProperties props) {
        return this.buildSpider(siteModel, props, false);
    }

    /**
     * 根据配置构造所需pipeline
     */
    private List<Pipeline> buildPipeline(SiteModel siteModel) {
        return Lists.newArrayList(
            new SimplePipeline()
        );
    }

    private Spider buildSpider(SiteModel siteModel, ProcessorProperties props, boolean onceOnly) {
        SimpleProcessor simpleProcessor = new SimpleProcessor(siteModel);
        simpleProcessor.setOnceOnly(onceOnly);

        // FIXME 应该在 SimpleProcessor 构造函数中实现这段逻辑
        Site site = simpleProcessor.getSite();
        site.addHeader("User-Agent", UserAgents.get());
        site.setDomain(siteModel.getSiteName());
        site.setSleepTime(props.getSleepTime()); // 每次抓取完畢，休息
        site.setRetrySleepTime(props.getRetrySleepTime()); // 重试休息时间：30s
        site.setRetryTimes(props.getRetryTimes()); // 重试 10次
        site.setTimeOut(props.getTimeout()); // 超时时间 30s

        Spider spider = new Spider(simpleProcessor);
//        AbstractSpider spider = new MyRedisSchedulerSpider(simpleProcessor, (MyRedisScheduler) scheduler);
        buildPipeline(siteModel).forEach(spider::addPipeline);
        spider.addUrl(siteModel.getUrl()).thread(executorService, props.getThreadNum());
        // 重复执行的任务（比如抓取评论），需要在抓取完毕时，结束本次抓取，开启新的抓取任务
        spider.setExitWhenComplete(siteModel.isRecycle());

        //
        DefaultMonitorListener.regist(spider);

        return spider;
    }


}
