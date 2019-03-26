package cn.cjp.spider.util;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;

public class IntSplitter {

    public static List<Integer> split(String str, String sp) {
        Iterable<String> splits    = Splitter.on(str).split(sp);
        List<Integer>    splitNums = new ArrayList<>();
        splits.forEach(s -> splitNums.add(Integer.parseInt(s)));
        return splitNums;
    }

}
