package cn.cjp.spider.core;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.security.MD5Encoder;


import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Demo {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        System.out.println(RandomStringUtils.random(11, true, true));

        System.out.println(MD5Encoder.encode(("tfHETu1mENh" + System.currentTimeMillis() % 6).getBytes()));

    }

}
