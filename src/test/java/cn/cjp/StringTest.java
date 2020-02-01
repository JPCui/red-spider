package cn.cjp;

import org.junit.Test;

public class StringTest {

    @Test
    public void testMatch(){
        String m = "https://movie.douban.com/subject/(\\d*)(/)+((\\?)?|\\?.*)";
        String s = "https://movie.douban.com/subject/24847340/?";
        System.out.println(s.matches(m));
        s = "https://movie.douban.com/subject/24847340/?xxx";
        System.out.println(s.matches(m));
        s = "https://movie.douban.com/subject/24847340/xxx";
        System.out.println(s.matches(m));
    }

}
