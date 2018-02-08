package cn.cjp.app.web.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import cn.cjp.app.exception.ServiceException;
import cn.cjp.app.model.response.Response;
import cn.cjp.app.util.ResponseUtil;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

    private static final String _500 = "/error/500";
    private static final String _404 = "/error/404";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView notFound(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Response response = Response.success("什么都没有");
        return ResponseUtil.get(response, _404);
    }

    @ExceptionHandler(ServiceException.class)
    public ModelAndView serviceException(ServiceException e) {
        Response response = Response.fail(e.getMessage());
        response.setCode(e.getCode());
        if (response.getCode() == 404) {
            return ResponseUtil.get(response, _404);
        }
        return ResponseUtil.get(response, _500);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView processUploadError(MaxUploadSizeExceededException ex) {
        Response response = Response.fail("上传文件过大");
        return ResponseUtil.get(response, _500);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView processUnexpectedError(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Response response = Response.fail("什...什么情况");
        return ResponseUtil.get(response, _500);
    }

}
