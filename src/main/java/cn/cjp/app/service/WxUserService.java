package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.doc.WxUserDoc;
import cn.cjp.app.repository.WxUserRepository;
import cn.cjp.wechat.WechatProperties;

@Service
public class WxUserService {

    @Autowired
    WechatProperties wechatProperties;

    @Autowired
    WxUserRepository wxUserRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public void save(WxUserDoc wxUserDoc) {
        wxUserRepository.save(wxUserDoc);
    }

    public WxUserDoc findOneByOpenid(String openid) {
        WxUserDoc wxUserDoc = mongoTemplate.findOne(Query.query(Criteria.where("openid").is(openid).and("appid").is(wechatProperties.getAppId())),
                WxUserDoc.class);
        return wxUserDoc;
    }

}
