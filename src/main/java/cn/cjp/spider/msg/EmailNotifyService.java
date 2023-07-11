package cn.cjp.spider.msg;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotifyService implements AbstractMsgNotifyService {

    final StringRedisTemplate redisTemplate;

    @Override
    public void send(List<String> toUsers, String title, String content) {

        MailAccount account = new MailAccount();
        account.setHost("smtp.qq.com");
        account.setPort(587);
        account.setAuth(true);
        account.setFrom("624498030@qq.com");
        account.setUser("624498030@qq.com");
        account.setPass("cogqsnusvkgebbbc"); //密码
        account.setSslEnable(false);

//        List<String> listeners = Lists.newArrayList("624498030@qq.com", "18238819901@163.com", "416557132@qq.com");
        for (String e : toUsers) {
            try {
                MailUtil.send(
                    account,
                    CollUtil.newArrayList(e),
                    title, content, true);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
