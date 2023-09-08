package cn.cjp;

import java.nio.charset.Charset;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.springframework.util.StreamUtils;

public class StringTest {

    @Test
    @SneakyThrows
    public void testMatch() {

        HttpGet httpGet = new HttpGet("https://api.bilibili.com/x/polymer/web-dynamic/v1/feed/space?offset=&host_mid=525121722");

        CloseableHttpClient   client = HttpClients.createDefault();
        CloseableHttpResponse resp   = client.execute(httpGet);

        System.out.println(resp.getStatusLine());
        System.out.println(StreamUtils.copyToString(resp.getEntity().getContent(), Charset.defaultCharset()));

        resp.close();
        client.close();

    }

}
