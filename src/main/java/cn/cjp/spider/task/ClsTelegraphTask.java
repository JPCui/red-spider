package cn.cjp.spider.task;

import cn.cjp.spider.task.entity.ClsTelegraphEntity;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class ClsTelegraphTask {

    long lastTime = System.currentTimeMillis() / 1000;

    public void run() {

        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "https://www.cls.cn/telegraph");
        headers.put("Content-Type", "application/json;charset=utf-8");

        Map<String, String> cookies = new HashMap<>();

        String url = String.format(
            "https://www.cls.cn/nodeapi/updateTelegraphList?app=CailianpressWeb&category=&hasFirstVipArticle=1&lastTime=%s&os=web&rn=20&subscribedColumnIds=&sv=7.7.5&sign=536cf019a34ced17436b12535cf12853",
            lastTime);

        Response resp = get(url, headers, cookies);
        String   body = resp.body();

        JSONObject json  = JSON.parseObject(body);
        Integer    error = json.getInteger("error");
        if (Objects.equals(error, 0)) {
            JSONArray rollData = json.getJSONObject("data").getJSONArray("roll_data");
            int       size     = rollData.size();
            for (int i = 0; i < size; i++) {
                JSONObject item = rollData.getJSONObject(i);

                String id = item.getInteger("id").toString();
                // 高亮
                Integer recommend = item.getInteger("recommend");
                String  content   = item.getString("content");

                ClsTelegraphEntity entity = new ClsTelegraphEntity();
                entity.setId(id);
                entity.setContent(content);
                entity.setHighlight(Objects.equals(recommend, 1) ? 1 : 0);


            }

        }


    }

    public void parse() {

    }

    @SneakyThrows
    public static Response get(String url, Map<String, String> headers, Map<String, String> cookies) {
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
                .headers(headers)
                .cookies(cookies)
                .execute();
            return response;
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
