package cn.cjp;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 测试下 nashorn 渲染前端页面
 */
@Slf4j
public class NashornTest {

    private static RestTemplate disabledValidTlsCert() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(csf)
            .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

    public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        RestTemplate restTemplate = disabledValidTlsCert();

        // 拿易连客页面为例
        String doc = restTemplate
            .getForObject("https://chongqingiicloud.com/main/cloud-resources/detail?ProductUuid=cb789272-cb17-48e9-8897-e8e43098f61d", String.class);

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine        nashorn             = scriptEngineManager.getEngineByName("nashorn");

        try {
            Object eval = nashorn.eval(doc);
            System.out.println(eval);
        } catch (ScriptException e) {
            System.out.println("执行脚本错误: " + e.getMessage());
        }

    }

}
