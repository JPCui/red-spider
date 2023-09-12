package cn.cjp.spider.manage;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.core.http.UserAgents;
import cn.cjp.spider.core.model.SiteModel;
import cn.cjp.spider.core.pipeline.PipelineFactory;
import cn.cjp.spider.core.processor.html.SimpleProcessor;
import cn.cjp.spider.core.scheduler.MyRedisScheduler;
import cn.cjp.spider.core.spider.AbstractSpider;
import cn.cjp.spider.core.spider.MyRedisSchedulerSpider;
import cn.cjp.spider.core.spider.listener.DefaultMonitorListener;
import cn.cjp.spider.dto.ProcessorProperties;
import cn.cjp.spider.exception.ServiceException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.Scheduler;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
@RequiredArgsConstructor
public class SpiderManager {

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    final ExecutorService executorService = Executors.newCachedThreadPool();

    final SpiderMonitor monitor;

    final Scheduler scheduler;

    final SiteManager siteManager;

    final PipelineFactory pipelineFactory;

    public void startAll() {
        ProcessorProperties props = new ProcessorProperties();

        SpiderConfig.PAGE_RULES.forEach((siteName, siteModel) -> {
            if (!monitor.isRunning(siteModel)) {
                start(siteName);
                log.info(String.format("spider[%s] start.", siteName));
            }
            log.info(String.format("spider[%s] is already start.", siteName));
        });
    }

    public void start(String siteName) {
        SiteModel siteModel = SpiderConfig.PAGE_RULES.get(siteName);
        siteModel.setStarted(true);
        try {
            this.lock.writeLock().lock();
            if (!monitor.isRunning(siteModel)) {
                Spider spider = initProcessor(siteModel, new ProcessorProperties());
                monitor.push(siteModel, spider);
                spider.start();
                log.info(String.format("spider[%s] start.", siteModel.getSiteName()));
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void stop(String siteName) {
        try {
            this.lock.writeLock().lock();
            SiteModel siteModel = SpiderConfig.PAGE_RULES.get(siteName);
            siteModel.setStarted(false);
            Optional.ofNullable(monitor.pop(siteModel)).ifPresent(Spider::stop);
            log.info(String.format("spider[%s] stop.", siteName));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    private SiteModel getSiteModel(String siteName) {
        return SpiderConfig.PAGE_RULES.get(siteName);
    }

    /**
     * FIXME 1 如果配置在下次运行时才生效？需要先停止，然后修改配置信息，最后重新运行
     *
     * TODO 2 当前修改配置是临时保存在内存里，需要切换为数据库操作
     */
    public void config(String siteName, ProcessorProperties props) {
        SiteModel siteModel = getSiteModel(siteName);
        if (monitor.isRunning(siteModel)) {
            Spider spider = monitor.get(siteModel);
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
        return siteModel.getPipelines().stream()
            .map(pipelineFactory::get)
            .collect(Collectors.toList());
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

//        Spider spider = new Spider(simpleProcessor);
        AbstractSpider spider = new MyRedisSchedulerSpider(simpleProcessor, (MyRedisScheduler) scheduler);
        buildPipeline(siteModel).forEach(spider::addPipeline);
        spider.addUrl(siteModel.getUrl()).thread(executorService, props.getThreadNum());
        // 重复执行的任务（比如抓取评论），需要在抓取完毕时，结束本次抓取，开启新的抓取任务
//        spider.setExitWhenComplete(siteModel.isRecycle());
        spider.setExitWhenComplete(false);

        // 设置本地代理
        HttpClientDownloader downloader = new HttpClientDownloader();
        downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 7890)));
        spider.setDownloader(downloader);

        //
        DefaultMonitorListener.regist(spider);

        return spider;
    }

}
