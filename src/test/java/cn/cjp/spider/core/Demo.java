package cn.cjp.spider.core;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Demo {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {


		System.out.println();

		String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^";
		for (int i = 0; i < s.length(); i++) {
			System.out.print('\'');
			System.out.print(s.charAt(i));
			System.out.print('\'');
			System.out.print(',');
		}
		System.out.println();

		for (int i = 0; i < 68; i++) {
			System.out.print(i+1);
			System.out.print(',');
		}
		System.out.println();

	}

}
