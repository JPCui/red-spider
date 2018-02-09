package cn.cjp.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.cjp.app.model.doc.WxUserDoc;
import cn.cjp.app.repository.WxUserRepository;

@Service
public class WxUserService {

    @Autowired
    WxUserRepository wxUserRepository;

    public void save(WxUserDoc wxUserDoc) {
        wxUserRepository.save(wxUserDoc);
    }

}
