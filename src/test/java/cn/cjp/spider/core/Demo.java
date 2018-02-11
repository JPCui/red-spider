package cn.cjp.spider.core;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import cn.cjp.app.model.doc.DocUtil;
import cn.cjp.app.model.doc.ReaderRecord;

public class Demo {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IllegalAccessException {

        System.out.println(DocUtil.bean2DBObject(new ReaderRecord()));

    }

}
