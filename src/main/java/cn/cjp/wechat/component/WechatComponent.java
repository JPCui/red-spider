package cn.cjp.wechat.component;

import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.qq.weixin.mp.aes.AesException;

import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

@Component
public class WechatComponent {

	@Value("${wechat.appId}")
	private String appId;

	@Value("${wechat.appsecret}")
	private String appSecret;

	@Value("${wechat.valid.token}")
	private String validToken;

	@Value("${wechat.valid.encodingAESKey}")
	private String validEncodingAESKey;

	/**
	 * 验签
	 *
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @return
	 * @throws AesException
	 */
	public String checkSignature(String signature, String timestamp, String nonce, String echostr) throws AesException {
		String genSign = SignatureUtil.generateEventMessageSignature(validToken, timestamp, nonce);
		return genSign.equals(signature) ? echostr : "";
	}

	public void textMessage(OutputStream os, String requestMessage, String responseMessage) {
		EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, requestMessage);
		XMLMessage responseMessageXML = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(),
				responseMessage);
		responseMessageXML.outputStreamWrite(os);
	}

	public void menus() {
		// MenuAPI.menuGet()
	}

}
