package cn.cjp.spider.msg;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Service
@RequiredArgsConstructor
@Slf4j
public class FeishuRobotNotifyService implements AbstractMsgNotifyService {

    @SneakyThrows
    @Override
    public void send(List<String> toUsers, String title, String content) {
        // 默认配置为自建应用
        Client client = Client.newBuilder("cli_a4fee1847f1e500b", "txTI7rF3PvOFnniCDcaU9eECwvUPXSiy")
            .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。
            .build();

        CreateMessageReqBody messageReqBody = CreateMessageReqBody.newBuilder()
            .msgType("text")
            .content(title + "\r\n" + content)
            .receiveId("")
            .build();
        CreateMessageReq messageReq = CreateMessageReq.newBuilder()
            .receiveIdType(CreateMessageReceiveIdTypeEnum.CHAT_ID)
            .createMessageReqBody(messageReqBody)
            .build();
        client.im().message().create(messageReq);

    }

}
