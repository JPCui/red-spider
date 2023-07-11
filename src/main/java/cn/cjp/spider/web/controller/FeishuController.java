package cn.cjp.spider.web.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feishu")
@Slf4j
public class FeishuController {

    @PostMapping("/robot/valid")
    public JSONObject validRobot(@RequestBody JSONObject json) {
        log.info("robot valid: " + json);
        String challenge = json.getString("challenge");

        JSONObject resp = new JSONObject();
        resp.put("challenge", challenge);
        return resp;
    }


}
