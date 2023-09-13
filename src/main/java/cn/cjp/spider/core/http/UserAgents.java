package cn.cjp.spider.core.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Slf4j
public class UserAgents {

    private static final List<String> userAgents = new ArrayList<>(200);

    static {
        try {

            Resource       resource = new PathMatchingResourcePatternResolver().getResource("classpath:user-agent.conf");
            BufferedReader reader   = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                userAgents.add(line);
            }
        } catch (IOException e) {
            log.error(String.format("user-agent.conf read fail"), e);
        }
    }

    /**
     * get a random user-agent
     */
    public static String get() {
        int i = userAgents.size();
        return userAgents.get(RandomUtils.nextInt(i));
    }


}
