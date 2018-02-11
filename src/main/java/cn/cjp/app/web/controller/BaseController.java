package cn.cjp.app.web.controller;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.cjp.app.config.AppConfig;
import cn.cjp.app.config.Symphony;
import cn.cjp.app.model.request.LoginUser;
import cn.cjp.app.util.CookieHelper;
import cn.cjp.app.util.DesHelper;
import cn.cjp.app.util.JwtHelper;
import cn.cjp.app.util.RequestHelper;
import cn.cjp.utils.JacksonUtil;
import cn.cjp.utils.StringUtil;

/**
 * <br>
 * 与当前登陆相关的接口需要加上 @AuthPassport 注解， controller 需要继承 BaseController ，通过调用
 * getCurrentUser 获取当前登陆用户
 *
 * @author sucre
 */
public abstract class BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    static final int SECOND_7_DAYS = 7 * 24 * 3600;

    @Autowired
    private Symphony symphony;

    public void saveLoginCookie(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser)
        throws Exception {

        int expire = SECOND_7_DAYS;

        String jwtLoginToken = JwtHelper.encode(symphony.getLogin().getSecret(),
            JacksonUtil.toMap(loginUser));
        CookieHelper.addCookieByDomain(request, response, AppConfig.ACCESS_TOKEN, jwtLoginToken, expire);

        String accessUserId = DesHelper.encrypt(loginUser.getUserId().toString(), AppConfig.ENCRYPT_USER_ID_KEY);
        CookieHelper.addCookieByDomain(request, response, AppConfig.ACCESS_USER_ID, accessUserId, expire);
    }

    /**
     * 获取当前登陆用户
     */
    public LoginUser getCurrentUser() {
        HttpServletRequest request = this.getCurrentRequest();
        String jwt = CookieHelper.getCookieValueByName(request, AppConfig.ACCESS_TOKEN);
        if (!StringUtil.isEmpty(jwt)) {
            Map<String, Object> currentUserMap = JwtHelper.decode(symphony.getLogin().getSecret(), jwt);
            LoginUser loginUser = JacksonUtil.fromMapToObj(currentUserMap, LoginUser.class);
            return loginUser;
        }
        return null;
    }

    public HttpServletRequest getCurrentRequest() {
        return RequestHelper.getCurrentRequst();
    }

    /**
     * 将逗号分隔字符串转换成Long of List.
     */
    protected List<Long> longsFromString(String str) {

        if (Strings.isNullOrEmpty(str)) {
            return new ArrayList<>();
        }

        final String[] split = str.split(",");

        return Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
    }

    /**
     * 将逗号分隔字符串转换成Integer of List.
     */
    protected List<Integer> integersFromString(String idStr) {

        if (Strings.isNullOrEmpty(idStr)) {
            return new ArrayList<>();
        }

        final String[] split = idStr.split(",");

        return Arrays.stream(split).map(Integer::valueOf).collect(Collectors.toList());
    }

    /**
     * 返回已逗号(,)为delimiter的字符串.
     *
     * @return null,"",或者,号分隔字符串.
     */
    protected String stringFormList(List<?> list) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return "";
        }

        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    protected void removeLoginCookie(HttpServletRequest request, HttpServletResponse response) {

        String hostName = RequestHelper.getMainHostName(request);
        CookieHelper.deleteCookieByNameAndDomain(response, hostName, AppConfig.ACCESS_TOKEN);
    }

}
