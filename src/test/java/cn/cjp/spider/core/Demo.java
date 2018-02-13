package cn.cjp.spider.core;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cn.cjp.wechat.enumeration.MsgType;

public class Demo {

	public static void main(String[] args)
			throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {

		System.out.println(MsgType.fromDescription("TEXT"));

	}

}
