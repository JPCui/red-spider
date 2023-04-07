package cn.cjp.wx.msg.service;

import cn.cjp.spider.msg.AbstractMsgNotifyService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts.MassMsgType;
import me.chanjar.weixin.mp.api.WxMpMassMessageService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpTemplateMsgService;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
@RequiredArgsConstructor
public class MsgService implements AbstractMsgNotifyService {

    private final WxMpService wxMpService;

    @SneakyThrows
    public void send(List<String> toUsers, String title, String content) {
        WxMpTemplateMsgService msgService = wxMpService.getTemplateMsgService();
        WxMpTemplateMessage message = WxMpTemplateMessage.builder()
            .templateId("ujgnUiErFakMhqze8nakDqoIxKK0e53VxG_322dqrWs")
            .toUser("isSendAll")
            .data(Collections.singletonList(
                new WxMpTemplateData("content", content + "(mb)")
            ))
            .build();
        String rs = msgService.sendTemplateMsg(message);
        log.debug(rs);

        WxMpMassTagMessage massMsgReq = new WxMpMassTagMessage();
        massMsgReq.setMsgType(MassMsgType.TEXT);
        massMsgReq.setSendAll(true);
        massMsgReq.setContent(content);
        WxMpMassMessageService massMessageService = wxMpService.getMassMessageService();
        massMessageService.massGroupMessageSend(massMsgReq);
    }


}
