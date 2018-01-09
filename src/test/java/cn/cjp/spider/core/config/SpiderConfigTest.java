package cn.cjp.spider.core.config;

import cn.cjp.utils.JacksonUtil;
import cn.cjp.utils.Logger;
import org.junit.Test;

public class SpiderConfigTest {

    public static final Logger LOGGER = Logger.getLogger(SpiderConfigTest.class);

    @Test
    public void test() {
        LOGGER.info(JacksonUtil.toJson(SpiderConfig.PAGE_RULES));
        LOGGER.info(JacksonUtil.toJson(SpiderConfig.PARSE_RULES));
    }

}
