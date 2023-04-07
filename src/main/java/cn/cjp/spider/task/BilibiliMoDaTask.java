package cn.cjp.spider.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
public class BilibiliMoDaTask {

    /**
     * for debug
     */
    private static final String K_SMTH_STOCK_TEST     = "stock.test";
    /**
     * 通知的邮箱
     */
    private static final String K_SMTH_NOTIFY_EMAIL   = "task.bilibili.moda.notify_email";
    /**
     * 监听的smth用户ID
     */
    private static final String K_SMTH_LISTEN_USER_ID = "task.bilibili.moda.listen_user";
    private static final String K_SMTH_PWD            = "conf.mysmth";
    private static final String HK_SMTH_KEY           = "UTMPKEY";
    private static final String HK_SMTH_NUM           = "UTMPNUM";

    private static boolean forTest       = false;
    private static boolean isLoginFailed = false;

    private Map<String, String> cookies = new HashMap<>();

    static final String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    static final String PATTERN_HMS     = "HH:mm:ss";
    static final String PATTERN_YMD     = "yyyy-MM-dd";

    static int retry = 0;

    final StringRedisTemplate redisTemplate;

    public BilibiliMoDaTask(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        String forTestStr = redisTemplate.opsForValue().get(K_SMTH_STOCK_TEST);
        if (forTestStr != null) {
            forTest = Boolean.valueOf(forTestStr);
        }
    }

    private String escape(String s) {
        // 去掉不可见字符
        s = s.replaceAll(" ", "");
        return s.trim();
    }

    private void initCookie() {

        String   c     = "buvid3=A571FFFE-67F8-D9D0-D8D7-32B84ED291C557053infoc; b_nut=1666084857; _uuid=EB10102DE8-BD15-876D-DC94-D1339AD2F5F657892infoc; fingerprint=4a0e58ccf35b89468fe0862d5199b976; buvid_fp_plain=undefined; SESSDATA=f41fa54d%2C1681638461%2Cee4ee%2Aa1; bili_jct=aa5635cee65dbb9162ddabe27695385c; DedeUserID=96918058; DedeUserID__ckMd5=d13f881e25b6ce9e; buvid_fp=4a0e58ccf35b89468fe0862d5199b976; nostalgia_conf=-1; CURRENT_PID=02503bc0-c868-11ed-97d8-25c3fcaee565; CURRENT_FNVAL=4048; rpdid=|(~J~JmRmR~0J'uY~mRY|lJ|; sid=84j3i316; i-wanna-go-back=-1; b_ut=5; b_lsid=D1B82288_1874B993682; header_theme_version=CLOSE; home_feed_column=5; innersign=1; buvid4=E58D58DB-1E3D-F9BF-E48F-C7F86AEBFF5100378-022012714-Qq6NpevE95yb%2FImr47FGLQ%3D%3D";
        String[] split = c.split("; ");
        for (String s : split) {
            String[] arr = s.split("=");
            cookies.put(arr[0], arr[1]);
        }
    }

    public static void main(String[] args) {
        new BilibiliMoDaTask(new StringRedisTemplate());
    }

    private void setTitle(String rpId, String title) {
        redisTemplate.opsForValue().set(String.format("bilibili:moda:replies:%s", rpId), title);
    }

    private String getTitle(String rpId) {
        return redisTemplate.opsForValue().get(String.format("bilibili:moda:replies:%s", rpId));
    }

    private boolean isRead(String rpId) {
        Long flag = redisTemplate.opsForSet().add("bilibili:moda:replies:read", rpId);
        return !(flag != null && flag > 0L);
    }

    private void setParent(String rpId, String pid) {
        redisTemplate.opsForValue().set(String.format("bilibili:moda:replies:%s:parent", rpId), pid);
    }

    private String getParentId(String rpid) {
        return redisTemplate.opsForValue().get(String.format("bilibili:moda:replies:%s:parent", rpid));
    }

    private void parseReply(JSONObject parent, JSONObject item) {
        Long    rpid        = item.getLong("rpid");
        Boolean top         = item.getBoolean("top");
        String  title       = item.getJSONObject("content").getString("message");
        String  uid         = item.getJSONObject("member").getString("mid");
        String  uname       = item.getJSONObject("member").getString("uname");
        long    postTimeStr = item.getLong("ctime") * 1000;
        Date    now         = new Date();

        if (parent == null) {
            log.info("parse main: " + uname + " - " + title);
        } else {
            log.info("parse: " + uname + " - " + title);
        }

        title = "[" + uname + "]" + title;
        if (top != null && top) {
            // 置顶评论，加上前缀标识
            title = "[↑]" + title;
        }

        // 时间太久远就跳过
        if (now.getTime() - postTimeStr > 1000 * 3600 * 24 * 3) {
            return;
        }

        if (isRead(rpid.toString())) {
            return;
        }
        // 保存帖子内容
        setTitle(rpid.toString(), title);

        Long parentId = item.getLong("parent");
        if (parentId != null && !parentId.equals(0L)) {
            setParent(rpid.toString(), parentId.toString());
        } else if (parent != null) {
            parentId = parent.getLong("rpid");
            setParent(rpid.toString(), parentId.toString());
        } else {
            parentId = 0L;
        }

        retry = 0;

        if (shouldFollow(item)) {
            log.info("a post found: {} {}", "[" + uname + "] ", rpid + " - " + title);

            StringBuilder titles = new StringBuilder(title);
            String        pid    = getParentId(rpid.toString());
            while (true) {
                if (pid != null && !pid.equals("0")) {
                    titles.append("\n\n\t|-\t ").append(getTitle(parentId.toString()));
                    pid = getParentId(pid);
                } else {
                    break;
                }
            }
            log.info("\nsend a post: {}\n", titles.toString());
            String timeStr = DateFormatUtils.format(postTimeStr, PATTERN_YMD_HMS);
            try {
                sendMail("[" + timeStr + "] " + title, titles.toString());
            } catch (Exception e) {
                redisTemplate.opsForSet().remove("bilibili:moda:replies:read", rpid + "");
            }
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
        // 傻狍子X
        if(uid.equals("691652013")) {
            return true;
        }
        // 超过60个字也关注一下
        if (title.length() > 60) {
            return true;
        }

        return false;
    }

    @SneakyThrows
    private void listenPost(String url) {
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
                return;
            }
            if (!json.containsKey("data")) {
                return;
            }
            JSONArray lines = json.getJSONObject("data").getJSONArray("replies");
            if (lines == null) {
                return;
            }
            if (json.getJSONObject("data").containsKey("top")
                && json.getJSONObject("data").getJSONObject("top").containsKey("upper")) {
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
                for (int pn = lastPage; pn > 0 && pn >= lastPage - 5; pn--) {
                    String rootId = item.getString("root_str");
                    if (rootId.equals("0")) {
                        rootId = item.getString("rpid_str");
                    }
                    String replyUrl = String.format(
                        "https://api.bilibili.com/x/v2/reply/reply?csrf=aa5635cee65dbb9162ddabe27695385c&oid=%s&pn=%s&ps=10&root=%s&type=17&_=%s",
                        item.getString("oid"), pn, rootId, now);
                    listenPost(replyUrl);
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void execute() {
        if (isLoginFailed) {
            return;
        }
//        login();
        initCookie();

        List<String> urls = Arrays.asList(
            "https://api.bilibili.com/x/v2/reply/main?next=0&type=17&oid=780591610288144421&mode=2&plat=1"
        );
        urls.forEach(this::listenPost);

    }

    private String[] initNotifyEmail() {
        Set<String> members = redisTemplate.opsForSet().members(K_SMTH_NOTIFY_EMAIL);

        if (members == null || members.isEmpty()) {
            members = new HashSet<>(Collections.singletonList("624498030@qq.com"));
        }
        return members.toArray(new String[]{});
    }

    public void sendMail(String title, String content) {
        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(587);
        account.setAuth(true);
        account.setFrom("624498030@qq.com");
        account.setUser("624498030@qq.com");
        account.setPass("cogqsnusvkgebbbc"); //密码
        account.setSslEnable(false);

        List<String> listeners = Lists.newArrayList(initNotifyEmail());
//        List<String> listeners = Lists.newArrayList("624498030@qq.com", "18238819901@163.com", "416557132@qq.com");
        for (String e : listeners) {
            try {
                MailUtil.send(
                    account,
                    CollUtil.newArrayList(e),
                    title, content, false);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

    }


}
