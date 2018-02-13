package cn.cjp.wechat.component;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.qq.weixin.mp.aes.AesException;

import cn.cjp.app.model.doc.ReaderRecord;
import cn.cjp.app.model.doc.WxUserDoc;
import cn.cjp.app.model.response.SectionResponse;
import cn.cjp.app.service.BookService;
import cn.cjp.app.service.ReaderRecordService;
import cn.cjp.app.service.WxUserService;
import cn.cjp.robot.tuling.api.TulingApi;
import cn.cjp.utils.Logger;
import cn.cjp.utils.StringUtil;
import cn.cjp.wechat.MsgUtil;
import cn.cjp.wechat.UserUtil;
import cn.cjp.wechat.WechatProperties;
import cn.cjp.wechat.enumeration.Event;
import cn.cjp.wechat.enumeration.MenuKey;
import cn.cjp.wechat.enumeration.MsgType;
import weixin.popular.api.UserAPI;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.message.message.TextMessage.Text;
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

	@Autowired
	BookService bookService;

	@Autowired
	ReaderRecordService readerRecordService;

	@Autowired
	TulingApi tulingApi;

	public User getUserInfo(String openid) {
		Token token = wechatTokenService.getAccessToken();

		User userInfo = UserAPI.userInfo(token.getAccess_token(), openid);

		return userInfo;
	}

	/**
	 * 验签
	 */
	public String checkSignature(String signature, String timestamp, String nonce, String echostr) throws AesException {
		String genSign = SignatureUtil.generateEventMessageSignature(wechatProperties.getValid().getToken(), timestamp,
				nonce);
		return genSign.equals(signature) ? echostr : "";
	}

	/**
	 * @deprecated use {@link #chat(EventMessage)}
	 */
	public void textMessage(OutputStream os, String requestMessage, String responseMessage) {
		EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, requestMessage);
		XMLMessage responseMessageXML = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(),
				responseMessage);
		responseMessageXML.outputStreamWrite(os);
	}

	public XMLMessage chat(EventMessage requestMessage) throws IOException {

		LOGGER.info("wx event: " + JSON.toJSONString(requestMessage));

		XMLMessage responseMessage = null;

		MsgType msgType = MsgType.fromDescription(requestMessage.getMsgType().toUpperCase());
		if (msgType != null) {
			switch (msgType) {
			case TEXT: {
				responseMessage = this.chatText(requestMessage);
				break;
			}
			case EVENT: {
				String event = requestMessage.getEvent();
				Event eventEnum = Event.fromDescription(event.toUpperCase());
				if (eventEnum != null) {
					switch (eventEnum) {
					case SUBSCRIBE: {
						responseMessage = this.chatOnSub(requestMessage);
						break;
					}
					case CLICK: {
						responseMessage = this.chatOnClink(requestMessage);
						break;
					}
					case VIEW:
					default: {
						responseMessage = this.chatUnsupport(requestMessage);
						break;
					}
					}
				} else {
					LOGGER.error(String.format("wx unknown event: %s", requestMessage.getEvent()));
					responseMessage = this.chatUnsupport(requestMessage);
				}
				break;
			}
			default: {
				responseMessage = this.chatUnsupport(requestMessage);
				break;
			}
			}
		} else {
			LOGGER.error(String.format("wx unknown msg type: %s", requestMessage.getMsgType()));
			responseMessage = this.chatUnsupport(requestMessage);
		}

		return responseMessage;
	}

	private XMLMessage chatOnClink(EventMessage requestMessage) {
		String responseMsg = null;

		String menuKeyStr = requestMessage.getEventKey();
		MenuKey menuKey = MenuKey.fromDescription(menuKeyStr);

		if (menuKey != null) {
			switch (menuKey) {
			case BOOK_READ: {
				ReaderRecord readerRecord = readerRecordService.findLatestByOpenid(requestMessage.getFromUserName());
				SectionResponse sectionResponse = bookService.getSectionByBookDocIdAndIndex(readerRecord.getBookDocId(),
						readerRecord.getIndex());
				String[] contents = sectionResponse.getContent();
				// 不能超过 600字
				responseMsg = contents[readerRecord.getSecIndex()].substring(0, 600);

				break;
			}
			case BOOK_READ_PREV: {
				break;
			}
			case BOOK_READ_NEXT: {
				break;
			}
			}

		}

		if (!StringUtil.isEmpty(responseMsg)) {

			Text text = new Text();
			text.setContent(responseMsg);
			XMLTextMessage responseMessage = new XMLTextMessage(requestMessage.getFromUserName(),
					requestMessage.getToUserName(), responseMsg);
			return responseMessage;
		}
		return null;
	}

	private XMLMessage chatText(EventMessage requestMessage) throws IOException {

		String msg = tulingApi.request(requestMessage.getContent());
		if (StringUtil.isEmpty(msg)) {
			msg = wechatProperties.getMsg().getDft();
		}
		XMLMessage message = MsgUtil.handlerTulingText(requestMessage.getFromUserName(), requestMessage.getToUserName(),
				msg);

		return message;
	}

	private XMLTextMessage chatUnsupport(EventMessage requestMessage) {
		String msg = wechatProperties.getMsg().getUnsupport();

		XMLTextMessage responseMessage = new XMLTextMessage(requestMessage.getFromUserName(),
				requestMessage.getToUserName(), msg);
		return responseMessage;
	}

	private XMLTextMessage chatOnSub(EventMessage requestMessage) {
		User user = this.getUserInfo(requestMessage.getFromUserName());

		WxUserDoc wxUserDoc = UserUtil.toWxUserDoc(user);
		wxUserDoc.setAppid(wechatProperties.getAppId());
		wxUserService.save(wxUserDoc);

		XMLTextMessage responseMessage = new XMLTextMessage(requestMessage.getFromUserName(),
				requestMessage.getToUserName(), wechatProperties.getMsg().getOnSubscribe());
		return responseMessage;

	}

}
