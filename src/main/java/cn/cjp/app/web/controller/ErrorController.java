package cn.cjp.app.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;

import cn.cjp.app.exception.ServiceException;

@Controller
@RequestMapping
public class ErrorController {

    @RequestMapping("404")
    public ModelAndView _404() throws ClassNotFoundException, FileNotFoundException {
        throw new ServiceException(404, "found 404");
    }

    @RequestMapping("500")
    public ModelAndView _500() throws Exception {
        throw new Exception("found 500");
    }


}
