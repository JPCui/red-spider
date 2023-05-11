package cn.cjp.spider.task;

import cn.cjp.spider.msg.AbstractMsgNotifyService;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 帖子监听
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XueqMoDaTask {

    long EXPIRE_7_DAYS = 1000 * 3600 * 24 * 7;

    /**
     * 通知的邮箱
     */
    private static final String K_XQ_NOTIFY_EMAIL      = "task.xueq.moda.notify_email";
    private static final String K_XQ_MODA_DYNAMIC_READ = "xueqiu:moda:dynamic:read";
    /**
     * 监听的smth用户ID
     */
    private static final String K_XQ_LISTEN_USER_ID    = "task.xueq.moda.listen_user";

    private static boolean isLoginFailed = false;

    private Map<String, String> cookies = new HashMap<>();

    static int retry = 0;

    final StringRedisTemplate            redisTemplate;
    final List<AbstractMsgNotifyService> msgNotifyServices;

    private void initCookie() {
        String   c     = "device_id=5857516b932dbd529789db131ef819ca; Hm_lvt_1db88642e346389874251b5a1eded6e3=1682228074; xq_a_token=70d1ac7706ebbfa03535e4ea3854cee918fb4563; xqat=70d1ac7706ebbfa03535e4ea3854cee918fb4563; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOjIwMjAwNDMzMDMsImlzcyI6InVjIiwiZXhwIjoxNjg1OTQyODgzLCJjdG0iOjE2ODMzNTA4ODMzMjQsImNpZCI6ImQ5ZDBuNEFadXAifQ.n3u93lPY0EV6jtmCv_IKrMZzLQr6jmAgZ2ptFNe5WE3qPyOmDP1g8qawQVSbj5Yd0ZPu1rZh4HN5g9uxgHWUMK2kKlGK0Ce9na1gReDwd8wcqRomiKvhKpOdkIdy2tWtUBFE3L9qqG5dszHqFnl3qpt3EEC1KozbgwsGBr7MKVecl4WFMbu4faX7bPcoLhGcdjzWDrjx0eI6MTzpHrq3P9pT_UVtEJyK3QGHwTljvtQ-7Bbx5VpAo8D9cENRFdzVOv9zHIs2IAknZfdts9VSJEV7zpGhKHbaLu8kprjtXeVz4-Jqt0WCgxE-QJrDIAaTiXF9Rjmiog3B2lAZb7va7w; xq_r_token=e277b8302cf49e2fe2ca47de9e9488c9e72bfb87; xq_is_login=1; u=2020043303; s=bl128q7zze; bid=a113c937042621d4710b05ae39f78e22_lhbjo664; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1683528484; acw_tc=276077a716837123151237779e8fb5fc0720b8576229b01e4253713c1ab55e";
        String[] split = c.split("; ");
        for (String s : split) {
            String[] arr = s.split("=");
            cookies.put(arr[0], arr[1]);
        }
    }
    private String escape(String s) {
        // 去掉不可见字符
        s = s.replaceAll(" ", "");
        return s.trim();
    }

    @SneakyThrows
    private JSONObject listenPost(String url) {
        Thread.sleep(RandomUtil.randomLong(2000L));
        long now = System.currentTimeMillis() / 1000;

        try {
            Response response = Jsoup
                .connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .method(Method.GET)
                .referrer("https://xueqiu.com/u/3598214045")
                .header("user-agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36")
                .header("Host", "xueqiu.com")
//                .header("referer", "https://api.xueq.com/")
                .header("x-requested-with", "XMLHttpRequest")
                .cookies(cookies)
                .execute();
            String     text = response.body();
            JSONObject json = JSON.parseObject(escape(text));

            return json;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Scheduled(fixedDelay = 60_000)
    public void execute() {
        if (isLoginFailed) {
            return;
        }
//        login();
        initCookie();

        Set<String> urls = new HashSet<>();
        urls.add("https://xueqiu.com/v4/statuses/user_timeline.json?page=1&user_id=3598214045");
        urls.forEach(url -> {
            JSONObject obj = listenPost(url);
            if (obj == null) {
                sendMsg("error: xueqiu", String.format("nothing found, url: %s", url));
                return;
            }
            handleDynamic(obj);
        });

    }

    private boolean isDynamicRead(String id) {
        Long flag = redisTemplate.opsForSet().add(K_XQ_MODA_DYNAMIC_READ, id);
        return !(flag != null && flag > 0L);
    }

    private void handleDynamic(JSONObject json) {
        JSONArray items = json.getJSONArray("statuses");

        int size = items.size();
        for (int i = 0; i < size; i++) {
            JSONObject item = items.getJSONObject(i);
            String     id   = item.getString("id");

            if (!isDynamicRead(id)) {
                this.parseUnreadDynamic(id, item);
            }
        }


    }

    private void parseUnreadDynamic(String id, JSONObject item) {
        // 动态类型（DYNAMIC_TYPE_WORD: 文字；DYNAMIC_TYPE_AV：视频;）
        String text = item.getString("description");
        String title = "雪球:" + text;
        String content = text + "\r\n > https://xueqiu.com/u/3598214045";

        sendMsg(title, content);
    }

    private String[] initNotifyEmail() {
        Set<String> members = redisTemplate.opsForSet().members(K_XQ_NOTIFY_EMAIL);

        if (members == null || members.isEmpty()) {
            members = new HashSet<>(Collections.singletonList("624498030@qq.com"));
        }
        return members.toArray(new String[]{});
    }

    public void sendMsg(String title, String content) {
        List<String> listeners = Lists.newArrayList(initNotifyEmail());
        for (AbstractMsgNotifyService service : msgNotifyServices) {
            service.send(listeners, title, content);
        }

    }


}
