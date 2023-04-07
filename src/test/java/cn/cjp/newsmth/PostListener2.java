package cn.cjp.newsmth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

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
public class PostListener2 implements Task {

    static final Jedis jedis = new Jedis("localhost");

    private Map<String, String> cookies = new HashMap<>();

    String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    String PATTERN_HMS     = "HH:mm:ss";
    String PATTERN_YMD     = "yyyy-MM-dd";

    static int retry = 0;

    static {
        jedis.auth("123456");
    }

    private void initCookie() {
        JSONObject json = JSON.parseObject("{\"main[UTMPUSERID]\":\"sucre01\",\"main[UTMPKEY]\":\"99216559\",\"main[UTMPNUM]\":\"15162\"}");
        json.keySet().forEach(k -> {
            cookies.put(k, json.getString(k));
        });
    }

    private String formatTime(String time) {
        if (time.length() == 8) {
            String prefix = DateFormatUtils.format(new Date(), PATTERN_YMD);
            time = prefix + " " + time;
        }
        if (time.length() == 10) {
            time = time + " 00:00:00";
        }
        return time;
    }

    private void login() {
        if (cookies.isEmpty()) {
            _login();
        }
    }

    private void _login() {
        try {
            log.info("重新登录...");
            Response response = Jsoup.connect("https://www.mysmth.net/nForum/user/ajax_login.json")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .method(Method.POST)
                .header("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("user-agent", "iZSM/559 CFNetwork/1240.0.4 Darwin/20.5.0")
                .header("origin", "https://www.mysmth.net")
                .header("referer", "https://www.mysmth.net/nForum/")
                .header("x-requested-with", "XMLHttpRequest")
                .data("ticket", "")
                .data("randstr", "")
                .data("id", "sucre01")
                .data("passwd", FileUtils.readFileToString(new File("/Users/sucre/.mysmth.pwd")).trim())
                .data("mode", "0")
                .data("CookieDate", "0")
                .execute();

            cookies = response.cookies();
            log.debug(JSON.toJSONString(cookies));

            JSONObject resp = JSON.parseObject(response.body());
            if (!resp.getString("ajax_code").equalsIgnoreCase("0005")) {
                log.warn("登录失败..., {}", resp.toJSONString());
                sendMail("login fail", resp.toJSONString());
                System.exit(-1);
            }

        } catch (RuntimeException | IOException e) {
            log.warn(e.getMessage(), e);
        }
    }

    private String escape(String s) {
        // 去掉不可见字符
        s = s.replaceAll(" ", "");
        return s.trim();
    }


    private void listenPost() {
        String[] aus = new String[]{"qqman", "beinghalf"};
        Stream.of(aus).forEach(this::listenPost);
    }

    private void listenPost(String au) {
        long now = System.currentTimeMillis() / 1000;

        try {
            Response response = Jsoup
                .connect("https://www.mysmth.net/nForum/s/article?b=Stock&au=" + au)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(true)
                .method(Method.GET)
                .header("authority", "www.mysmth.net")
                .header("user-agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.88 Safari/537.36")
                .header("origin", "https://www.mysmth.net")
                .header("referer", "https://www.mysmth.net/nForum/")
                .header("x-requested-with", "XMLHttpRequest")
                .cookies(cookies)
                .execute();
            Document doc = response.parse();

            Elements lines = doc.select("tbody tr");
            int      size  = lines.size();
            log.info("----------------------------------------");
            for (int i = 0; i < size; i++) {
                if (i >= 5) {
                    break;
                }
                Element line = lines.get(i);
                if (line.text().contains("没有搜索到任何主题")) {
                    retry++;
                    if (retry > 5) {
                        log.warn("登录失效？尝试重新登录");
                        log.info(doc.html());
                        sendMail("login fail", lines.html());
//                        cookies = Collections.emptyMap();
                        System.exit(-2);
                    }
                    break;
                }
                retry = 0;
                String title       = line.select(".title_9 a").get(0).text().trim();
                String href        = line.select(".title_9 a").attr("href").trim();
                String id          = href.substring(href.lastIndexOf("/") + 1);
                String postTimeStr = line.select(".title_10").get(0).text().trim();
                postTimeStr = escape(postTimeStr);
                postTimeStr = formatTime(postTimeStr);
                String newReplyTime = line.select(".title_10").get(1).text().trim();
                newReplyTime = formatTime(newReplyTime);
                String newReplyUser = line.select(".title_12").get(1).select("a").get(0).text().trim();

                long   pubTime = DateUtils.parseDate(postTimeStr, PATTERN_YMD_HMS).getTime() / 1000;
                String timeStr = DateFormatUtils.format(pubTime * 1000, "MM-dd HH:mm:ss");
                log.info("a post found: id: {} {}", id, "[" + timeStr + "] " + title);

                Long flag = jedis.sadd("alreadySendPosts", id);
                if (flag > 0) {
                    log.info("send a post: {} {}", id, title);
                    try {
                        sendMail("[" + timeStr + "] " + title, title);
                    } catch (Exception e) {
                        jedis.srem("alreadySendPosts", id);
                    }
                }

            }


        } catch (IOException | ParseException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void execute() {
//        login();
        initCookie();
        listenPost();


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

        List<String> listeners = Lists.newArrayList("624498030@qq.com");
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


    public static void main(String[] args) {
        log.info("begin");

        PostListener2 task = new PostListener2();
        task.execute();

        CronUtil.schedule("post_listener", "*/20 * * * * *", task);
        CronUtil.setMatchSecond(true);
        CronUtil.start();

//        new PostListener2().run();

    }
}
