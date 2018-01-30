package cn.cjp.spider.manage.web.controller;

import cn.cjp.wechat.component.WechatComponent;
import com.qq.weixin.mp.aes.AesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import weixin.popular.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

@Controller("/wechat")
@RequestMapping
public class WechatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    WechatComponent wechatComponent;

    @RequestMapping(value = {"/event"}, method = RequestMethod.GET)
    @ResponseBody
    public String checkSignature(String signature, String timestamp, String nonce, String echostr) throws AesException {
        return wechatComponent.checkSignature(signature, timestamp, nonce, echostr);
    }

    @RequestMapping(value = {"/event"}, method = RequestMethod.POST)
    @ResponseBody
    public void checkSignature(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String message = StreamUtils.copyToString(request.getInputStream(), Charset.defaultCharset());
        LOGGER.info(String.format("rec wechat msg: %s", message));
        LOGGER.info(String.format("rec params: %s", request.getParameterMap()));

        String responseMsg = "嗯哼~";
        wechatComponent.textMessage(response.getOutputStream(), message, responseMsg);
        return;
    }

    @GetMapping("/menus")
    public void menus() {
        
    }


}
