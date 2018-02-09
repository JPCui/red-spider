package cn.cjp.wechat.component;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.core.cache.CacheManager;
import cn.cjp.wechat.WechatProperties;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.token.Token;

@Service
public class WechatTokenService {

    @Autowired
    WechatProperties wechatProperties;

    @Autowired
    CacheManager cacheManager;

    public Token getAccessToken() {
        String key = "wx:access_token";

        Token token = cacheManager.getCache(Token.class, key);

        if (token == null) {
            token = TokenAPI.token(wechatProperties.getAppId(), wechatProperties.getAppSecret());
        }

        cacheManager.saveCache(key, JSONObject.toJSONString(token), token.getExpires_in());

        return token;
    }

}
