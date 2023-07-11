package cn.cjp.spider.msg;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
@Slf4j
public class NeverNotifyService implements AbstractMsgNotifyService {

    final StringRedisTemplate redisTemplate;

    @Override
    public void send(List<String> toUsers, String title, String content) {

        for (String e : toUsers) {
            log.info(String.format("send to %s: %s - %s", e, title, content));
        }
    }
}
