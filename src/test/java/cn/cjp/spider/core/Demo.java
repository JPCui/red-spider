package cn.cjp.spider.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Demo {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

		System.out.println(
				"http://www.99lib.net/book/index.php?page=2".matches("http://www.99lib.net/book/index.php?page=(.*)"));

	}

}
