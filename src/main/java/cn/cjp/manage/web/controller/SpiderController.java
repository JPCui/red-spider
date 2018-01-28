package cn.cjp.manage.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/spider")
@RestController
public class SpiderController {

	@RequestMapping("/status")
	public String getStatus() {
		return "ok";
	}

}
