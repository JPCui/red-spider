package cn.cjp.spider.core;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.cjp.app.model.doc.DocUtil;
import cn.cjp.app.model.doc.ReaderRecord;

public class Demo {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {

        List<ReaderRecord> list = new ArrayList<>();

        System.out.println(list.getClass().getTypeName());
        System.out.println(list.getClass().getDeclaringClass());
        System.out.println(list.getClass().getGenericSuperclass());


    }

}
