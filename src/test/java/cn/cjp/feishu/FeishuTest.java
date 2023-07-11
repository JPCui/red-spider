package cn.cjp.feishu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lark.oapi.Client;
import com.lark.oapi.core.response.RawResponse;
import com.lark.oapi.core.token.AccessTokenType;
import com.lark.oapi.service.contact.v3.enums.ListUserUserIdTypeEnum;
import com.lark.oapi.service.contact.v3.model.ListUserReq;
import com.lark.oapi.service.contact.v3.model.ListUserResp;
import com.lark.oapi.service.im.v1.enums.ListChatUserIdTypeEnum;
import com.lark.oapi.service.im.v1.model.ListChatReq;
import com.lark.oapi.service.im.v1.model.ListChatResp;
import lombok.SneakyThrows;
import org.junit.Test;

public class FeishuTest {

    @Test
    @SneakyThrows
    public void testForChat() {

        // 默认配置为自建应用
        Client client = Client.newBuilder("cli_a4fee1847f1e500b", "txTI7rF3PvOFnniCDcaU9eECwvUPXSiy")
//            .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。
            .build();

        ListChatReq listChatReq = ListChatReq.newBuilder()
            .userIdType(ListChatUserIdTypeEnum.OPEN_ID)
            .pageSize(10)
            .build();
        ListChatResp listChatResp = client.im().chat().list(listChatReq);

        System.out.println(JSON.toJSONString(listChatResp));

        // =================

        ListUserReq listUserReq = ListUserReq.newBuilder()
            .userIdType(ListUserUserIdTypeEnum.UNION_ID)
            .departmentId("0")
            .build();
        ListUserResp listUserResp = client.contact().user().list(listUserReq);
        System.out.println(JSON.toJSONString(listUserResp));
        // =================

        JSONObject batchMessageRequest = new JSONObject();
        batchMessageRequest.put("msg_type", "text");
        batchMessageRequest.put("content", new JSONObject().fluentPut("text", "xxx"));
        batchMessageRequest.put("union_ids", Lists.newArrayList("on_009cd74f46cb583663e5d552bb5693cb"));

        RawResponse resp = client.post("https://open.feishu.cn/open-apis/message/v4/batch_send/",
                                       batchMessageRequest,
                                       AccessTokenType.Tenant
        );

        System.out.println(resp.getStatusCode());
        System.out.println(resp.getRequestID());
        System.out.println(new String(resp.getBody()));

    }

}
