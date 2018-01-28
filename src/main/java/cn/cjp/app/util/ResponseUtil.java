package cn.cjp.app.util;

import org.springframework.web.servlet.ModelAndView;

import cn.cjp.app.model.response.Response;

public class ResponseUtil {

	public static ModelAndView get(Response<?> response, String view) {
		int code = response.getCode();
		Object data = response.getData();
		String msg = response.getMessage();

		ModelAndView mv = new ModelAndView(view);
		mv.addObject("code", code);
		mv.addObject("data", data);
		mv.addObject("msg", msg);
		return mv;
	}

}
