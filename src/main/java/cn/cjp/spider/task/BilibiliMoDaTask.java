package cn.cjp.spider.task;

import cn.cjp.spider.msg.AbstractMsgNotifyService;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 帖子监听
 *
 * 不再使用之前的模拟手机端的api来调接口，而使用直接爬取官网html的方式，这样我们就不用每次都去手机拿token，而是直接模拟登陆获取cookie
 *
 * https://exp.newsmth.net/board/members/3bcda03dcf4ca0e36c3cc96eaa4cf6f8
 *
 * 这里还有个站点，也可以用来爬数据 TODO
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BilibiliMoDaTask {

    long EXPIRE_7_DAYS = 1000 * 3600 * 24 * 7;

    /**
     * for debug
     */
    private static final String K_SMTH_STOCK_TEST = "stock.test";

    final static         Set<String> URL_DYNAMICS_ONLY_FANS   = new HashSet<>();
    /**
     * 通知的邮箱
     */
    private static final String      K_SMTH_NOTIFY_EMAIL      = "task.bilibili.moda.notify_email";
    private static final String      K_SMTH_MODA_REPLY        = "bilibili:moda:replies:%s";
    private static final String      K_SMTH_MODA_DYNAMIC_READ = "bilibili:moda:dynamic:read";
    private static final String      K_SMTH_MODA_REPLY_PARENT = "bilibili:moda:replies:%s:parent";
    /**
     * 监听的smth用户ID
     */
    private static final String      K_SMTH_LISTEN_USER_ID    = "task.bilibili.moda.listen_user";
    private static final String      K_SMTH_PWD               = "conf.mysmth";
    private static final String      HK_SMTH_KEY              = "UTMPKEY";
    private static final String      HK_SMTH_NUM              = "UTMPNUM";

    private static boolean isLoginFailed = false;

    private Map<String, String> cookies = new HashMap<>();

    static final String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    static final String PATTERN_HMS     = "HH:mm:ss";
    static final String PATTERN_YMD     = "yyyy-MM-dd";

    static int retry = 0;

    final StringRedisTemplate            redisTemplate;
    final List<AbstractMsgNotifyService> msgNotifyServices;

    private String escape(String s) {
        // 去掉不可见字符
        s = s.replaceAll(" ", "");
        return s.trim();
    }

    private void initCookie() {

        String   c     = "buvid3=C8222003-8A1D-C543-823B-244D427B1DE172523infoc; b_nut=1688532072; b_lsid=37C1991B_189245ABBCA; _uuid=51433B68-A2F4-43910-86C7-1010C768FDC61273429infoc; buvid_fp=4a0e58ccf35b89468fe0862d5199b976; buvid4=E58D58DB-1E3D-F9BF-E48F-C7F86AEBFF5100378-022012714-Qq6NpevE95yb%2FImr47FGLQ%3D%3D; SESSDATA=d3997c47%2C1704084099%2C38902%2A71VcoAKCo7ffmtNG3syBOxBTVTh14tTnOjSYCbTchkH6X_9AHxIwL21vKUKM5QDCwU0i5b4QAAOgA; bili_jct=80e9e07cb92bcd22f27cc9f6427feced; DedeUserID=96918058; DedeUserID__ckMd5=d13f881e25b6ce9e; CURRENT_FNVAL=4048; sid=6vl3w530; rpdid=|(kYkY)klkJu0J'uY))lJkukR; bp_video_offset_96918058=814731772524757000";
        String[] split = c.split("; ");
        for (String s : split) {
            String[] arr = s.split("=");
            cookies.put(arr[0], arr[1]);
        }
    }

    private void setTitle(String rpId, String title) {
        redisTemplate.opsForValue().set(String.format(K_SMTH_MODA_REPLY, rpId), title, Duration.of(EXPIRE_7_DAYS, ChronoUnit.MILLIS));
    }

    private String getTitle(String rpId) {
        return redisTemplate.opsForValue().get(String.format(K_SMTH_MODA_REPLY, rpId));
    }

    private boolean isCommentRead(String rpId) {
        Long flag = redisTemplate.opsForSet().add("bilibili:moda:replies:read", rpId);
        return !(flag != null && flag > 0L);
    }

    private boolean isDynamicRead(String id) {
        Long flag = redisTemplate.opsForSet().add(K_SMTH_MODA_DYNAMIC_READ, id);
        return !(flag != null && flag > 0L);
    }

    private void delRead(String rpId) {
        redisTemplate.opsForSet().remove("bilibili:moda:replies:read", rpId);
    }

    private void setParent(String rpId, String pid) {
        redisTemplate.opsForValue().set(String.format(K_SMTH_MODA_REPLY_PARENT, rpId), pid, Duration.of(EXPIRE_7_DAYS, ChronoUnit.MILLIS));
    }

    private String getParentId(String rpid) {
        return redisTemplate.opsForValue().get(String.format(K_SMTH_MODA_REPLY_PARENT, rpid));
    }

    private void parseReply(JSONObject parent, JSONObject item) {
        String  rpid        = item.getString("rpid_str");
        Boolean top         = item.getBoolean("top");
        String  title       = item.getJSONObject("content").getString("message");
        String  uid         = item.getJSONObject("member").getString("mid");
        String  uname       = item.getJSONObject("member").getString("uname");
        long    postTimeStr = item.getLong("ctime") * 1000;
        Date    now         = new Date();

        title = "[" + uname + "]" + title;
        if (top != null && top) {
            // 置顶评论，加上前缀标识
            title = "[↑]" + title;
        }

        // 时间太久远就跳过
        if (now.getTime() - postTimeStr > 1000 * 3600 * 24 * 3) {
            log.info(String.format("%s was outdated: [%s]%s", rpid, uname, title));
            return;
        }

        if (isCommentRead(rpid)) {
            log.info(String.format("%s was read before: [%s]%s", rpid, uname, title));
            return;
        }
        // 保存帖子内容
        setTitle(rpid, title);

        String parentId = item.getString("parent_str");
        if (parentId != null && !parentId.equals("0")) {
            setParent(rpid, parentId);
        } else if (parent != null) {
            parentId = parent.getString("rpid");
            setParent(rpid, parentId);
        }

        retry = 0;

        if (shouldFollow(item)) {
            log.info("a post found: {} {}", "[" + uname + "] ", rpid + " - " + title);

            StringBuilder titles = new StringBuilder(String.format("[%s]%s", rpid, title));
            String        pid    = getParentId(rpid);
            int           i      = 1;
            while (true) {
                if (pid != null && !pid.equals("0")) {
                    titles.append("\r\n<br/><br/>");
                    titles.append(StringUtils.repeat("&emsp;&emsp;", i++));
                    titles.append(String.format("[%s]%s", pid, getTitle(pid)));
                    pid = getParentId(pid);
                } else {
                    break;
                }
            }
            log.info("\nsend a post: {}\n", titles);
            String timeStr = DateFormatUtils.format(postTimeStr, PATTERN_YMD_HMS);
            try {
                sendMsg("[" + timeStr + "] " + title, titles.toString());
            } catch (Exception e) {
                delRead(rpid);
            }
        } else {
            log.info(String.format("%s was not follow: [%s]%s", rpid, uname, title));
        }

        JSONArray subItems = item.getJSONArray("replies");
        if (subItems != null) {
            for (int i = 0; i < subItems.size(); i++) {
                JSONObject subItem = subItems.getJSONObject(i);
                parseReply(item, subItem);
            }
        }

    }

    private boolean shouldFollow(JSONObject item) {
        String title = item.getJSONObject("content").getString("message");
        String uid   = item.getJSONObject("member").getString("mid");
        String uname = item.getJSONObject("member").getString("uname");

        // m大
        if (uid.equals("525121722")) {
            return true;
        }
        // 大师兄
        if (uid.equals("492309991")) {
            return true;
        }
        //
        if (uname.equals("打逗她") || uname.equals("想想莫大会怎么做") || uname.equals("丶C1trus")) {
            return true;
        }
        // 超过60个字也关注一下
        if (title.length() > 60) {
            return true;
        }

        return false;
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
                .header("user-agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36")
//                .header("origin", "api.bilibili.com")
//                .header("referer", "https://api.bilibili.com/")
                .header("x-requested-with", "XMLHttpRequest")
                .cookies(cookies)
                .execute();
            Document   doc  = response.parse();
            String     text = doc.text();
            JSONObject json = JSON.parseObject(text);

            if (!json.getInteger("code").equals(0)) {
                return json;
            }
            if (!json.containsKey("data")) {
                return json;
            }
            return json;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Scheduled(fixedDelay = 20_000)
    public void execute() {
        if (isLoginFailed) {
            return;
        }
//        login();
        initCookie();

        // 监听up的动态，主要看有没有开新的置顶帖子
        listenDynamic();

        Set<String> urls = new HashSet<>();
        // moda
        urls.add("https://api.bilibili.com/x/v2/reply/main?next=0&type=17&oid=793933281966948371&mode=2&plat=1");
        // dashixiong
        urls.add("https://api.bilibili.com/x/v2/reply/main?next=0&type=17&oid=816753847680630820&mode=2&plat=1");
        urls.addAll(URL_DYNAMICS_ONLY_FANS.stream().map(dynamicId -> {
            return String.format("https://api.bilibili.com/x/v2/reply/main?next=0&type=17&oid=%s&mode=2&plat=1", dynamicId);
        }).collect(Collectors.toList()));
        urls.forEach(url -> {
            handleComment(Objects.requireNonNull(listenPost(url)));
        });

    }

    private void handleComment(JSONObject json) {
        LocalDateTime now   = LocalDateTime.now();
        JSONArray     lines = json.getJSONObject("data").getJSONArray("replies");
        if (lines == null) {
            return;
        }
        if (json.getJSONObject("data").getJSONObject("top") != null
            && json.getJSONObject("data").getJSONObject("top").getJSONObject("upper") != null) {
            JSONObject upperItem = json.getJSONObject("data").getJSONObject("top").getJSONObject("upper");
            upperItem.put("top", true);
            lines.add(0, upperItem);
        }
        int size = lines.size();
        log.info("----------------------------------------");
        for (int i = 0; i < size; i++) {
            JSONObject item = lines.getJSONObject(i);
            this.parseReply(null, item);

            // reply总数
            Integer rcount = item.getInteger("rcount");
            if (rcount == null || rcount == 0) {
                continue;
            }
            int lastPage = rcount / 10 + 1;
            // 只取后5页
            String rootId = item.getString("root_str");
            if (rootId.equals("0")) {
                rootId = item.getString("rpid_str");
            }
            for (int pn = lastPage; pn > 0 && pn >= lastPage - 10; pn--) {
                String replyUrl = String.format(
                    "https://api.bilibili.com/x/v2/reply/reply?csrf=aa5635cee65dbb9162ddabe27695385c&oid=%s&pn=%s&ps=10&root=%s&type=17&_=%s",
                    item.getString("oid"), pn, rootId, now);
                listenPost(replyUrl);
            }
        }
    }

    private void listenDynamic() {
        List<String> urls = Arrays.asList(
            "https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space?offset=&host_mid=525121722",
            "https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space?offset=&host_mid=492309991"
        );
        urls.forEach(url -> {
            JSONObject dynamicJson = listenPost(url);
            handleDynamic(dynamicJson);
        });
    }

    private void handleDynamic(JSONObject json) {
        try {
            JSONArray items = json.getJSONObject("data").getJSONArray("items");

            int size = items.size();
            for (int i = 0; i < size; i++) {
                JSONObject item = items.getJSONObject(i);
                String     id   = item.getString("id_str");

                if (!isDynamicRead(id)) {
                    this.parseUnreadDynamic(id, item);
                }
            }
        } catch (RuntimeException e) {
            log.error(json.toString(), e);
        }


    }

    private void parseUnreadDynamic(String id, JSONObject item) {
        // 动态类型（DYNAMIC_TYPE_WORD: 文字；DYNAMIC_TYPE_AV：视频;）
        String type = item.getString("type");
        String text = null;

        Boolean isOnlyFans = item.getJSONObject("basic").getBoolean("is_only_fans");
        if (isOnlyFans != null && isOnlyFans) {
            URL_DYNAMICS_ONLY_FANS.add(id);
        }

        String author = item.getJSONObject("modules").getJSONObject("module_author").getString("name");

        switch (type) {
            // 视频
            case "DYNAMIC_TYPE_AV": {
                text = item.getJSONObject("modules").getJSONObject("module_dynamic").getJSONObject("major").getJSONObject("archive")
                    .getString("title");

                break;
            }
            // 文字
            case "DYNAMIC_TYPE_WORD":
                // 图文
            case "MAJOR_TYPE_DRAW":
                // 转发
            case "DYNAMIC_TYPE_FORWARD": {
                text = item.getJSONObject("modules").getJSONObject("module_dynamic").getJSONObject("desc")
                    .getString("text");

                break;
            }
            default: {
                text = "未知内容，前往B站查看";
                break;
            }
        }

        sendMsg("B站动态:" + author + "," + text, text);
    }

    private String[] initNotifyEmail() {
        Set<String> members = redisTemplate.opsForSet().members(K_SMTH_NOTIFY_EMAIL);

        if (members == null || members.isEmpty()) {
            members = new HashSet<>(Collections.singletonList("624498030@qq.com"));
        }
        return members.toArray(new String[]{});
    }

    public void sendMsg(String title, String content) {
        title = title.length() > 50 ? title.substring(0, 50) : title;
        List<String> listeners = Lists.newArrayList(initNotifyEmail());
        for (AbstractMsgNotifyService service : msgNotifyServices) {
            try {
                service.send(listeners, title, content);
            } catch (RuntimeException e) {
                log.error(e.getMessage(), e);
            }
        }

    }


}
