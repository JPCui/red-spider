package cn.cjp.spider.core;

import java.util.UUID;

public class Demo {

    public static void main(String[] args) {
        UUID uuid = UUID.nameUUIDFromBytes("asd".getBytes());
        
        System.out.println(UUID.nameUUIDFromBytes("asd".getBytes()));
    }

}
