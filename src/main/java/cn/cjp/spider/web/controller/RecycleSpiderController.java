package cn.cjp.spider.web.controller;

import cn.cjp.spider.dto.response.Response;
import cn.cjp.spider.manage.RecycleSpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recycle")
public class RecycleSpiderController {

    @Autowired
    RecycleSpiderService recycleSpiderService;

    @PostMapping
    public Response run() {
        recycleSpiderService.run();
        return Response.success();
    }

}
