package cn.cjp.spider.manage;

import cn.cjp.spider.core.model.SiteModel;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class SpiderMonitor {


    final Map<String, Spider> runningSpiders        = new HashMap<>();
    /**
     * 正在运行的循环任务spider
     */
    final Map<String, Spider> runningRecycleSpiders = new HashMap<>();

    public List<String> runningList() {
        return Lists.newArrayList(runningSpiders.keySet());
    }

    public boolean isRunning(SiteModel siteModel) {
        return this.runningSpiders.containsKey(siteModel.getSiteName());
    }

    /**
     *
     * @param siteModel
     * @param spider
     */
    public void push(SiteModel siteModel, Spider spider) {
        runningSpiders.put(siteModel.getSiteName(), spider);
        if (siteModel.isRecycle()) {
            runningRecycleSpiders.put(siteModel.getSiteName(), spider);
        }
    }

    public Spider get(SiteModel siteModel) {
        String siteName      = siteModel.getSiteName();
        return runningSpiders.get(siteName);
    }

    public Spider pop(SiteModel siteModel) {
        String siteName      = siteModel.getSiteName();
        Spider spider        = runningSpiders.remove(siteName);
        Spider recycleSpider = runningRecycleSpiders.remove(siteName);

        return spider == null ? recycleSpider : spider;
    }



}
