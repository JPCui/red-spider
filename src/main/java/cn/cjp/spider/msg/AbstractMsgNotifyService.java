package cn.cjp.spider.msg;

import java.util.List;

public interface AbstractMsgNotifyService {

    public void send(List<String> toUsers, String title, String content);

}
