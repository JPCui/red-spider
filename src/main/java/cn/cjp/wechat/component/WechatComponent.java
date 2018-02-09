package cn.cjp.wechat.component;

import com.alibaba.fastjson.JSON;
import com.qq.weixin.mp.aes.AesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

import cn.cjp.app.service.WxUserService;
import cn.cjp.utils.Logger;
import cn.cjp.wechat.UserUtil;
import cn.cjp.wechat.WechatProperties;
import cn.cjp.wechat.enumeration.Event;
import cn.cjp.wechat.enumeration.MsgType;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.token.Token;
import weixin.popular.bean.user.User;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

@Component
public class WechatComponent {

    private static final Logger LOGGER = Logger.getLogger(WechatComponent.class);

    @Autowired
    WechatProperties wechatProperties;

    @Autowired
    WechatTokenService wechatTokenService;

    @Autowired
    WxUserService wxUserService;

    public User getUserInfo(String openid) {
        Token token = wechatTokenService.getAccessToken();

        User userInfo = UserAPI.userInfo(token.getAccess_token(), openid);

        return userInfo;
    }

    /**
     * 验签
     */
    public String checkSignature(String signature, String timestamp, String nonce, String echostr) throws AesException {
        String genSign = SignatureUtil.generateEventMessageSignature(wechatProperties.getValid().getToken(), timestamp, nonce);
        return genSign.equals(signature) ? echostr : "";
    }

    public void textMessage(OutputStream os, String requestMessage, String responseMessage) {
        EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, requestMessage);
        XMLMessage responseMessageXML = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(),
                responseMessage);
        responseMessageXML.outputStreamWrite(os);
    }

    public EventMessage chat(EventMessage requestMessage) {

        LOGGER.info("wx event: " + JSON.toJSONString(requestMessage));

        EventMessage responseMessage = null;

        MsgType msgType = MsgType.fromDescription(requestMessage.getMsgType());
        if (msgType != null) {
            switch (msgType) {
                case text: {
                    responseMessage = this.chatText(requestMessage);
                    break;
                }
                case event: {
                    String event = requestMessage.getEvent();
                    Event eventEnum = Event.fromDescription(event);
                    if (eventEnum != null) {
                        switch (eventEnum) {
                            case subscribe: {
                                responseMessage = this.onSub(requestMessage);
                                break;
                            }
                            default: {
                                responseMessage = this.chatText(requestMessage);
                                break;
                            }
                        }
                    } else {
                        LOGGER.error(String.format("wx unknown event: %s", requestMessage.getEvent()));
                        responseMessage = this.chatText(requestMessage);
                    }
                    break;
                }
                default: {
                    responseMessage = this.chatText(requestMessage);
                    break;
                }
            }
        } else {
            LOGGER.error(String.format("wx unknown msg type: %s", requestMessage.getMsgType()));
            responseMessage = this.chatText(requestMessage);
        }

        return responseMessage;
    }

    private EventMessage chatText(EventMessage requestMessage) {

        String msg = wechatProperties.getMsg().getDft();

        EventMessage responseMessage = new EventMessage();
        responseMessage.setFromUserName(requestMessage.getToUserName());
        responseMessage.setToUserName(requestMessage.getFromUserName());
        responseMessage.setMsg(msg);
        return responseMessage;
    }

    private EventMessage onSub(EventMessage requestMessage) {

        User user = this.getUserInfo(requestMessage.getFromUserName());

        wxUserService.save(UserUtil.toWxUserDoc(user));

        EventMessage responseMessage = new EventMessage();
        responseMessage.setFromUserName(requestMessage.getToUserName());
        responseMessage.setToUserName(requestMessage.getFromUserName());
        responseMessage.setMsg(wechatProperties.getMsg().getOnSubscribe());
        return responseMessage;

    }


}
