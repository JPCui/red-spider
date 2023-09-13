package cn.cjp.spider.core.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * 凭证领域，场景：邮件pipeline需要邮件服务、飞书消息pipeline需要飞书app凭证等
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class CredentialDomain {

    String  id;
    String  host;
    int     port;
    Boolean auth;
    String  from;
    String  user;
    String  pass;
    Boolean sslEnable = false;

}
