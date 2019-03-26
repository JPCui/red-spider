package cn.cjp.spider.core.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.util.ResourceUtils;

@Slf4j
public class UserAgents {

    private static final List<String> userAgents = new ArrayList<>(200);

    static {
        try {
            File         userAgentConf = ResourceUtils.getFile("classpath:user-agent.conf");
            List<String> confs         = FileUtils.readLines(userAgentConf);
            userAgents.addAll(confs.stream().filter(s -> !s.startsWith("#") || !s.startsWith("//")).collect(Collectors.toList()));
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

    public static void main(String[] args) {
        System.out.println(get());
    }

}
