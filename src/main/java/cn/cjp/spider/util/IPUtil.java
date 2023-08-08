package cn.cjp.spider.util;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

@Slf4j
public class IPUtil {

    /**
     * @return province
     */
    public static String getCityByIp(String ip) {
        String     api  = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;
        Connection conn = Jsoup.connect(api).ignoreContentType(true).ignoreHttpErrors(true);
        conn.header("Content-Type", "application/json; charset=utf-8");
        try {
            String text = conn.get().text();
            if (text != null && text.startsWith("{")) {
                Map<String, Object> json = JSON.parseObject(text);
                return json.get("province").toString();
            }
        } catch (IOException e) {
            log.warn(e.toString(), e);
        }
        return null;
    }

    public static Map<String, String> getCityByIps(String[] ips) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Query ips(%d) info.", ips.length));
        }

        final ExecutorService       service = Executors.newCachedThreadPool();
        Map<String, Future<String>> futures = new HashMap<>();
        for (String ip : ips) {
            Future<String> future = service.submit(Task.newInstance(ip));
            futures.put(ip, future);
        }

        Map<String, String> infoMap = new HashMap<>();
        for (String ip : futures.keySet()) {
            Future<String> future = futures.get(ip);
            try {
                String info = future.get(5, TimeUnit.SECONDS);
                infoMap.put(ip, info);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
            }
        }
        service.shutdown();
        return infoMap;
    }

    public static void main(String[] args) {
        String[] ips = {"202.196.35.35", "202.196.35.36", "202.196.35.37", "202.196.35.37", "101.200.143.36",
                        "101.201.143.36"};
        Object r = getCityByIps(ips);
        log.info(r.toString());

        // for (String ip : ips) {
        // LOGGER.info(getCityByIp(ip));
        // }
    }

}

class Task implements Callable<String> {

    String ip;

    private Task() {
    }

    public static Task newInstance(String ip) {
        Task task = new Task();
        task.ip = ip;
        return task;
    }

    @Override
    public String call() throws Exception {
        return IPUtil.getCityByIp(ip);
    }

    public String getIp() {
        return ip;
    }

}
