package cn.cjp.robot.tuling.api;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.cjp.app.config.Symphony;

@Component
public class TulingApi {

	public static final int CODE_TEXT = 100000;

	public static final int CODE_LINK = 200000;

	public static final int CODE_NEWS = 302000;

	public static final int CODE_MENU = 308000;

	/**
	 * 儿童-儿歌
	 */
	public static final int CODE_CHILDREN_ERGE = 313000;

	/**
	 * 儿歌-诗歌
	 */
	public static final int CODE_CHILDREN_POETRY = 304000;

	@Autowired
	Symphony symphony;

	public String request(String info) throws IOException {
		String requestUrl = symphony.getTuling().getApi() + "?key=" + symphony.getTuling().getApikey() + "&info="
				+ info;
		String s = Jsoup.connect(requestUrl).get().text();
		return s;
	}

}
