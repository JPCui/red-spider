package cn.cjp.spider.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Demo {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("b");
        list.add("c");
        list.add("a");
        list.add("a");
        list.add("c");

        System.out.println(list);

        System.out.println(list.stream().distinct().collect(Collectors.toList()));

        System.out.println(list);
    }

}
