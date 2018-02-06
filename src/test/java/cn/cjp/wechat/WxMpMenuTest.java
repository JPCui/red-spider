package cn.cjp.wechat;

import org.junit.Test;
import org.springframework.util.StringUtils;

import weixin.popular.api.MenuAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.menu.Button;
import weixin.popular.bean.menu.MenuButtons;
import weixin.popular.bean.token.Token;

public class WxMpMenuTest {

    String appid = "wx46b87cd60d921ad1";
    String secret = "310318fe04525556d42ed82ef84ee607";

    @Test
    public void testAdd() {

        Button sub1 = new Button();
        sub1.setType("view");
        sub1.setName("View Book");
        sub1.setUrl("http://www.baidu.com/");

        Button sub2 = new Button();
        sub2.setType("click");
        sub2.setName("Click");
        sub2.setKey("click-01");

        Button sub3 = new Button();
        sub3.setType("pic_weixin");
        sub3.setName("PIC");
        sub3.setKey("click-02");

        MenuButtons btn1 = new MenuButtons();
        btn1.setButton(new Button[]{sub1, sub2, sub3});

        String accessToken = "6_6i2BjEpQ3mXkX6U8z-92dfA3cNBm1oQWIdZNqDckSHyl0EtR5QbNXYfH8oIH7paBMLzGCXs-ug0wQ-sQp" +
                "-s4hjNQPRGWEgKhOZOYcrgRjQajTce0nXy4nkM5IvhtQldDsYwROF7XPsmUwQAxLOXaACAGJS";
        if (StringUtils.isEmpty(accessToken)) {
            Token token = TokenAPI.token(appid, secret);
            System.out.println(token.getAccess_token());
            accessToken = token.getAccess_token();
        }

        MenuAPI.menuCreate(accessToken, btn1);
    }

}
