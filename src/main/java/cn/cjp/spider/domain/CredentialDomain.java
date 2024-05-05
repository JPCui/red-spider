package cn.cjp.spider.domain;

import cn.cjp.spider.infr.util.CredentialType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 凭证领域，场景：邮件pipeline需要邮件服务、飞书消息pipeline需要飞书app凭证等
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CredentialDomain {

    String         id;
    String         userId;
    CredentialType type;

    String  host;
    Integer port;
    Boolean auth;
    String  from;
    // for redis, mysql
    String  pass;
    // for redis, mysql
    String  user;
    // for redis, mysql
    Boolean sslEnable = false;

    // for mysql
    String url;

    public static CredentialDomain newEmailCredentialDomain(
        String id,
        String host,
        Integer port,
        Boolean auth,
        String from,
        String user,
        String pass,
        Boolean sslEnable
    ) {
        CredentialDomain domain = new CredentialDomain();
        domain.id = id;
        domain.host = host;
        domain.port = port;
        domain.auth = auth;
        domain.from = from;
        domain.user = user;
        domain.pass = pass;
        domain.sslEnable = sslEnable;
        return domain;
    }

}
