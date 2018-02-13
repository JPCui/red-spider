package cn.cjp.wechat.web.controller;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qq.weixin.mp.aes.AesException;

import cn.cjp.utils.Logger;
import cn.cjp.wechat.component.WechatComponent;
import io.swagger.annotations.Api;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.util.StreamUtils;
import weixin.popular.util.XMLConverUtil;

@RestController
@RequestMapping("/wx/mp")
@Api
public class WxMpController {

	private static final Logger LOGGER = Logger.getLogger(WxMpController.class);

	@Autowired
	WechatComponent wechatComponent;

	@GetMapping
	public String index(String signature, String timestamp, String nonce, String echostr) throws AesException {
		LOGGER.info(String.format("signature: %s, timestamp: %s, nonce: %s, echostr: %s", signature, timestamp, nonce,
				echostr));
		return wechatComponent.checkSignature(signature, timestamp, nonce, echostr);
	}

	@PostMapping
	public String index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestMessage = StreamUtils.copyToString(request.getInputStream(), Charset.defaultCharset());
		LOGGER.info(String.format("rec wechat msg: %s", requestMessage));
		LOGGER.info(String.format("rec params: %s", request.getParameterMap()));
		{
			// String responseMessage = "嘤嘤嘤~";
			// wechatComponent.textMessage(os, requestMessage, responseMessage);
		}

		ServletOutputStream os = response.getOutputStream();
		XMLMessage responseEventMessage = wechatComponent
				.chat(XMLConverUtil.convertToObject(EventMessage.class, requestMessage));
		responseEventMessage.outputStreamWrite(os);
		os.flush();
		return "";
	}

}
