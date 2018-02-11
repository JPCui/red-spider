package cn.cjp.app.util;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.cjp.app.config.AppConfig;

public class RequestHelper {

    private static Logger logger = LoggerFactory.getLogger(RequestHelper.class.getClass().getSimpleName());

    /**
     * 获取请求完整URL
     */
    public static String getCurrentRequestUrl(HttpServletRequest request) {
        StringBuffer requestUrl = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestUrl.append("?").append(request.getQueryString());
        }

        return requestUrl.toString();
    }

    /**
     * 获取请求域名
     */
    public static String getRequestDomain(HttpServletRequest request) {
        return request.getScheme().concat("://").concat(request.getServerName());
    }

    public static HttpServletRequest getCurrentRequst() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();
        return request;
    }

    public static HttpServletResponse getCurrentResponse() {
        HttpServletResponse request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getResponse();
        return request;
    }

    public static String getParameter(HttpServletRequest request, String name) {
        return getParameter(request, name, "");
    }

    public static String getParameter(HttpServletRequest request, String name, String defaultValue) {
        if (request != null) {
            String value = request.getParameter(name);
            if (Strings.isNullOrEmpty(value)) {
                return defaultValue;
            }

            return value;
        }

        return "";
    }

    public static void setNextUrlToSession(HttpServletRequest request, String nextUrl) {
        if (!Strings.isNullOrEmpty(nextUrl)) {
            request.getSession().setAttribute(AppConfig.LOGIN_NEXT_URL, nextUrl);
        }
    }

    public static String getNextUrlFromSession(HttpServletRequest request) {
        String nextUrl = (String) request.getSession().getAttribute(AppConfig.LOGIN_NEXT_URL);
        request.getSession().removeAttribute(AppConfig.LOGIN_NEXT_URL);
        return nextUrl;
    }

    public static boolean fromInnerIp(HttpServletRequest request) {
        return isInnerIp(RequestHelper.getIp(request));
    }

    public static boolean isInnerIp(String ip) {
        String reg = "^(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";// 正则表达式=。
        // =、懒得做文字处理了、
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(ip);
        return matcher.find() || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1");
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!Strings.isNullOrEmpty(ip) && !ip.equalsIgnoreCase("unKnown")) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (!Strings.isNullOrEmpty(ip) && !ip.equalsIgnoreCase("unKnown")) {
            return ip;
        }

        return request.getRemoteAddr();
    }

    /**
     * 判断是否是在微信浏览器中打开的
     */
    public static boolean inWeixin(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();

        return userAgent.indexOf("micromessenger") >= 0;
    }

    public static String getUserAgentString(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        return userAgent;
    }

    public static String getHostName(HttpServletRequest request) {
        String hostName = request.getServerName();
        if (isInnerIp(hostName)) {
            // 是IP
            return hostName;
        }
        return hostName;
    }

    /**
     * 获取主域名
     */
    public static String getMainHostName(HttpServletRequest request) {
        String hostName = request.getServerName();
        if (isInnerIp(hostName)) {
            return hostName;
        }
        String[] split = hostName.split("\\.");
        if (split.length > 2) {
            return split[split.length - 2] + "." + split[split.length - 1];
        } else if (split.length == 1) {
            return hostName;
        }
        return "";
    }
}
