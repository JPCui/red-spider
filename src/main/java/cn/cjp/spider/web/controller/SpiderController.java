package cn.cjp.spider.web.controller;

import cn.cjp.spider.core.config.SpiderConfig;
import cn.cjp.spider.dto.ProcessorProperties;
import cn.cjp.spider.dto.request.SpiderRequest.ConfigRequest;
import cn.cjp.spider.dto.request.SpiderRequest.RestartRequest;
import cn.cjp.spider.dto.request.SpiderRequest.RunRequest;
import cn.cjp.spider.dto.request.SpiderRequest.StartRequest;
import cn.cjp.spider.dto.request.SpiderRequest.StopRequest;
import cn.cjp.spider.dto.response.Response;
import cn.cjp.spider.manage.SpiderManager;
import cn.cjp.spider.manage.SpiderMonitor;
import com.alibaba.fastjson.JSONObject;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spider")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SpiderController {

    @Autowired
    final SpiderManager spiderManager;

    @Autowired
    final SpiderMonitor spiderMonitor;

    @GetMapping("/all")
    public Response all() {
        Collection<String> runningSpiders = spiderMonitor.runningList();

        JSONObject json = new JSONObject();
        SpiderConfig.PAGE_RULES.keySet().forEach(siteName -> {
            if (runningSpiders.contains(siteName)) {
                json.put(siteName, 1);
            } else {
                json.put(siteName, 0);
            }
        });

        return Response.success(json);
    }

    /**
     * 配置某个站点的爬取规则
     */
    @PostMapping("/config")
    public Response config(@RequestBody ConfigRequest configRequest) {
        String siteName = configRequest.getSiteName();

        ProcessorProperties props = new ProcessorProperties();
        BeanUtils.copyProperties(configRequest, props);
        spiderManager.config(siteName, props);
        return Response.success();
    }

    /**
     * 启动爬取任务
     */
    @PostMapping("/start")
    public Response start(@RequestBody StartRequest startRequest) {
        spiderManager.start(startRequest.getSiteName());

        return Response.success();
    }

    /**
     * 暂停爬取任务
     */
    @PostMapping("/stop")
    public Response stop(@RequestBody StopRequest stopRequest) {
        spiderManager.stop(stopRequest.getSiteName());
        return Response.success();
    }

    @PostMapping("/restart")
    public Response restart(@RequestBody RestartRequest restartRequest) {
        spiderManager.stop(restartRequest.getSiteName());
        spiderManager.start(restartRequest.getSiteName());
        return Response.success();
    }

    /**
     * 手动爬取指定URL
     *
     * 若无配置该url爬取规则，返回失败
     */
    @PostMapping("/run")
    public Response run(@RequestBody RunRequest runRequest) {
        spiderManager.run(runRequest.getUrl());
        return Response.success();
    }


}
