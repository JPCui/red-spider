package cn.cjp.spider.core;

import cn.cjp.spider.core.downloader.SimpleDownloader;
import cn.cjp.spider.core.processor.SimpleProcessor;
import us.codecraft.webmagic.Spider;

public class Demo {

    public static void main(String[] args) {
        SimpleProcessor processor = new SimpleProcessor();

        SimpleDownloader downloader = new SimpleDownloader();

        Spider.create(processor).setDownloader(downloader).run();
    }

}
