package cn.cjp.app.web.controller;

import com.alibaba.fastjson.JSON;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import cn.cjp.app.exception.ServiceException;

@Controller
@RequestMapping
public class TestController {

    @RequestMapping("json")
    @ResponseBody
    public Map<String, String> json() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        return map;
    }

    @RequestMapping("json_str")
    @ResponseBody
    public String jsonStr() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        return JSON.toJSONString(map);
    }

    @RequestMapping("404")
    public ModelAndView _404() throws ClassNotFoundException, FileNotFoundException {
        throw new ServiceException(404, "found 404");
    }

    @RequestMapping("500")
    public ModelAndView _500() throws Exception {
        throw new Exception("found 500");
    }


}
