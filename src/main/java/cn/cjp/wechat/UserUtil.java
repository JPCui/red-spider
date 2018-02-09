package cn.cjp.wechat;

import cn.cjp.app.model.doc.WxUserDoc;
import weixin.popular.bean.user.User;

public class UserUtil {

    public static WxUserDoc toWxUserDoc(User user) {
        WxUserDoc userDoc = new WxUserDoc();
        userDoc.setCity(user.getCity());
        userDoc.setCountry(user.getCountry());
        userDoc.setHeadimgurl(user.getHeadimgurl());
        userDoc.setNickname(user.getNickname());
        userDoc.setNicknameEmoji(user.getNickname_emoji());
        userDoc.setOpenid(user.getOpenid());
        userDoc.setProvince(user.getProvince());
        userDoc.setUnionid(user.getUnionid());
        userDoc.setSex(user.getSex());
        return userDoc;
    }
}
