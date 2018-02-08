package cn.cjp.wechat;

import org.junit.Test;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import cn.cjp.Server;
import weixin.popular.api.MenuAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.menu.Button;
import weixin.popular.bean.menu.MenuButtons;
import weixin.popular.bean.token.Token;

public class WxMpMenuTest {

	@Test
	public void testAdd() {

		Button sub1 = new Button();
		sub1.setType("view");
		sub1.setName("冰心全集第二卷");
		sub1.setUrl("http://test.betago2016.com/rs/book/5a668be494607b0508b5e160");

		Button sub2 = new Button();
		sub2.setType("view");
		sub2.setName("冰心文集第一卷");
		sub2.setUrl("http://test.betago2016.com/rs/book/5a669a0994607b0508b6280a");

		Button sub3 = new Button();
		sub3.setType("view");
		sub3.setName("解忧杂货店");
		sub3.setUrl("http://test.betago2016.com/rs/book/5a5ed420acc9ac15582c6839");

		// Button sub1 = new Button();
		// sub1.setType("view");
		// sub1.setName("View Book");
		// sub1.setUrl("http://www.baidu.com/");

		// Button sub2 = new Button();
		// sub2.setType("click");
		// sub2.setName("Click");
		// sub2.setKey("click-01");

		// Button sub3 = new Button();
		// sub3.setType("pic_weixin");
		// sub3.setName("PIC");
		// sub3.setKey("click-02");

		Button m1 = new Button();
		m1.setKey("m1");
		m1.setSub_button(Lists.newArrayList(sub1, sub2, sub3));

		MenuButtons btn1 = new MenuButtons();
		btn1.setButton(new Button[] { m1 });

		// String accessToken =
		// "6_6i2BjEpQ3mXkX6U8z-92dfA3cNBm1oQWIdZNqDckSHyl0EtR5QbNXYfH8oIH7paBMLzGCXs-ug0wQ-sQp"
		// +
		// "-s4hjNQPRGWEgKhOZOYcrgRjQajTce0nXy4nkM5IvhtQldDsYwROF7XPsmUwQAxLOXaACAGJS";
		String accessToken = null;
		if (StringUtils.isEmpty(accessToken)) {
			Token token = TokenAPI.token(Server.Wechat.appid, Server.Wechat.secret);
			System.out.println(token.getAccess_token());
			accessToken = token.getAccess_token();
		}

		MenuAPI.menuCreate(accessToken, btn1);
	}

}
