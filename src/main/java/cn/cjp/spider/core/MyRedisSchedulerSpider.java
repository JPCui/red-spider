package cn.cjp.spider.core;

import cn.cjp.spider.core.scheduler.MyRedisScheduler;
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
 *
 */
public class MyRedisSchedulerSpider extends us.codecraft.webmagic.Spider {

	public MyRedisSchedulerSpider(PageProcessor pageProcessor) {
		super(pageProcessor);
	}

	public Spider setScheduler(Scheduler scheduler) {
		// if (!(scheduler instanceof MyRedisScheduler)) {
		// throw new UnsupportedOperationException(
		// String.format("scheduler should implements %s",
		// MyRedisScheduler.class));
		// }
		return super.setScheduler(scheduler);
	}

	protected void onSuccess(Request request) {
		super.onSuccess(request);
		if (this.scheduler instanceof MyRedisScheduler) {
			((MyRedisScheduler) this.scheduler).onDownloadSuccess(request, this);
		}
	}

	protected void onDownloadSuccess(Request request, Page page) {
		if (site.getAcceptStatCode().contains(page.getStatusCode())) {
			pageProcessor.process(page);

			extractAndAddRequests(page, spawnUrl);
			if (!page.getResultItems().isSkip()) {
				for (Pipeline pipeline : pipelines) {
					pipeline.process(page.getResultItems(), this);
				}
			}
		} else {
			logger.info("page status code error, page {} , code: {}", request.getUrl(), page.getStatusCode());
		}
		sleep(site.getSleepTime());
		return;
	}

}
