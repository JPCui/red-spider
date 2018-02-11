package cn.cjp.app.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHelper {
    // private static TicketService ticketService =
    // BeanLoader.getBean(TicketService.class);

    /**
     * 设置cookie
     *
     * @param name   cookie名字
     * @param value  cookie值
     * @param maxAge cookie生命周期 以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String name, String value, String domain, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain(domain);
        if (maxAge > 0)
            cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // public static void addCookieByDomain(HttpServletRequest request,
    // HttpServletResponse response,
    // Map<String, String> kvs, int maxAge) {
    // Set<String> keySet = kvs.keySet();
    // for (String key : keySet) {
    // addCookieByDomain(request, response, key, kvs.get(key), maxAge);
    // }
    // }

    public static void addCookieByDomain(HttpServletRequest request, HttpServletResponse response, String name,
                                         String value, int maxAge) {
        String domain = RequestHelper.getMainHostName(request);
        addCookieByDomain(response, domain, name, value, maxAge);
    }

    /**
     * 设置cookie
     *
     * @param name   cookie名字
     * @param value  cookie值
     * @param maxAge cookie生命周期 以秒为单位
     */
    public static void addCookieByDomain(HttpServletResponse response, String domain, String name, String value,
                                         int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain(domain);
        if (maxAge > 0)
            cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     *
     * @param name cookie名字
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 根据名字获取cookie value
     *
     * @param name cookie名字
     * @return cookie value , null if not exist
     */
    public static String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = getCookieByName(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * 删除cookie
     */
    public static void deleteCookieByName(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Cookie cookie2 = new Cookie(name, null);
        cookie2.setPath("/");
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);
    }

    /**
     * 删除cookie
     */
    public static void deleteCookieByNameAndDomain(HttpServletResponse response, String domain, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        response.addCookie(cookie);

        Cookie cookie2 = new Cookie(name, null);
        cookie2.setPath("/");
        cookie2.setMaxAge(0);
        cookie2.setDomain(domain);
        response.addCookie(cookie2);
    }

    // public static void setLoginCookie(HttpServletResponse response,UserEntity
    // user){
//        // 设置用户Cookie
////        String ticket = ticketService.createUserEntityTicket(user);
////        int maxAge = ConstConfig.maxAge;
//        Cookie cookie = new Cookie(ConstConfig.userCookieName, ticket);
//        cookie.setMaxAge(maxAge);
//        cookie.setPath("/"); //原wechat没有设置域名，只有paht
//        response.addCookie(cookie);
//    }

//    public static void setWxCookie(HttpServletResponse response) {
//        // 设置用户Cookie
//        int maxAge = 1 * 60;//1分钟
    // Cookie cookie = new Cookie(ConstConfig.wxloginCookieName,
    // String.valueOf(System.currentTimeMillis()));
//        cookie.setMaxAge(maxAge);
//        cookie.setPath("/"); //原wechat没有设置域名，只有paht
//        response.addCookie(cookie);
//    }

    /**
     * 将cookie封装到Map里面
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

}
