package cn.cjp.spider.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> list = new ArrayList<>();
        list.add("b");
        list.add("c");
        list.add("a");
        list.add("a");
        list.add("c");
        list = null;

        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("C:/test.txt"), true), "UTF-8"));
        printWriter.println(list);
        printWriter.flush();
        printWriter.close();

    }

}
