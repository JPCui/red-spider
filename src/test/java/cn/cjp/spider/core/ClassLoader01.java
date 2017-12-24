package cn.cjp.spider.core;

public class ClassLoader01 {

	public static void main(String[] args) {
		System.out.println(B.a);
	}

}

class A {

	static {
		System.out.println("A:");
	}

}

class B extends A {

	static int a = 1;

	static {
		System.out.println("B:");
	}

}