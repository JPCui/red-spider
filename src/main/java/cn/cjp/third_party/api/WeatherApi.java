package cn.cjp.third_party.api;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;

import java.io.IOException;

import cn.cjp.utils.Logger;

public class WeatherApi {

    private static final Logger LOGGER = Logger.getLogger(WeatherApi.class);

    protected static Header jsonHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

    public enum URL {
        cityinfo("http://www.weather.com.cn/data/cityinfo/{code}.html",
                new String[]{"{code}"}), img("http://m.weather.com.cn/img/{img}.gif", new String[]{"{img}"});

        String url;

        String[] replaces;

        private URL(String url, String[] replaces) {
            this.url = url;
            this.replaces = replaces;
        }

        public static String url(URL url, String... replaces) {
            String apiUrl = url.url;
            for (int i = 0; i < url.replaces.length; i++) {
                apiUrl = apiUrl.replace(url.replaces[i], replaces[i]);
            }
            LOGGER.info(apiUrl);
            return apiUrl;
        }
    }

    public static String get(String code) {
        String url = URL.url(URL.cityinfo, code);
        String jsonStr = null;
        try {
            jsonStr = Jsoup.connect(url).ignoreContentType(true).header("Accept", "application/json")
                    .header("Accept-Encoding", "*") // EOF
                    .get().text();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return jsonStr;
    }
}
