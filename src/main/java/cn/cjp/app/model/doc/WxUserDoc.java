package cn.cjp.app.model.doc;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "wx_user")
@CompoundIndexes({@CompoundIndex(name = "uk_appid_unionid", def = "{'appid': 1, 'unionid': 1}", unique = true)})
@Data
@EqualsAndHashCode(callSuper = false)
public class WxUserDoc extends BaseDoc {

    private String city;

    private String country;

    private String groupid;

    private String headimgurl;

    private String nickname;

    private String nicknameEmoji;

    private String openid;

    private String province;

    private String unionid;

    private int sex;

    private String appid;


}
