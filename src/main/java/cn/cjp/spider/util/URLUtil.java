package cn.cjp.spider.util;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {

	/**
	 * 返回去掉锚点的URL，即去掉'#'及'#'后面的字符串
	 *
	 * @param urlStr
	 *            原URL
	 * @return
	 */
	public static String url(String urlStr) {
		URL url = null;
		try {
			url = new URL(urlStr);
			String pureUrl = url.getProtocol() + "://" + url.getHost() + url.getPath();
			if (url.getQuery() != null) {
				pureUrl += "?" + url.getQuery();
			}
			return pureUrl;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取域名
	 *
	 * @param urlStr
	 * @return
	 */
	public static String getHost(String urlStr) {
		try {
			return new URL(urlStr).getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 计算相对路径
	 *
	 * <pre>
	 * eg:
	 * http://www.xxx.com/a + other = http://www.xxx.com/other
	 * http://www.xxx.com/a/ + other = http://www.xxx.com/a/other
	 * http://www.xxx.com/a + /other = http://www.xxx.com/other
	 * http://www.xxx.com/a/ + /other = http://www.xxx.com/other
	 * </pre>
	 *
	 * @param url
	 *            当前URL
	 * @param other
	 *            相对资源路径
	 * @return
	 * @throws MalformedURLException
	 */
	public static String relative(String url, String other) throws MalformedURLException {
		String urlEnd = url.substring(url.length() - 1);
		String otherStart = other.substring(0, 1);

		String r = null;
		if (otherStart.equals("/")) {
			// r = domain + other
			URL aUrl = new URL(url);
			StringBuilder sb = new StringBuilder(50);
			sb.append(aUrl.getProtocol());
			sb.append("://");
			sb.append(getHost(url));
			if (aUrl.getPort() > 0) {
				sb.append(":");
				sb.append(aUrl.getPort());
			}
			sb.append(other);
			r = sb.toString();
		} else {
			if (urlEnd.equals("/")) {
				r = url + other;
			} else {
				r = url.substring(0, url.lastIndexOf("/") + 1) + other;
			}
		}

		return r;
	}

	public static void main(String[] args) throws MalformedURLException {
		String url = "http://m.baidu.com:80/asd/aaa";
		String other = "###";

		System.out.println(relative(url, other));
	}

}
