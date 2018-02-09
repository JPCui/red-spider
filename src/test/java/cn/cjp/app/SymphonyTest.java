package cn.cjp.app;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import cn.cjp.SpiderAppApplication;
import cn.cjp.app.config.Symphony;
import cn.cjp.utils.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SpiderAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SymphonyTest {

    private Logger LOGGER = Logger.getLogger(SymphonyTest.class);

    @Autowired
    Symphony symphony;

    @Test
    public void testCondition() {
        Assert.assertNotNull(symphony.getServerPath());
    }

}
