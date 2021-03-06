package cn.cjp.spider.util;

import com.alibaba.fastjson.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucre
 */
@Slf4j
public class _99libUtil {

    /**
     * 用来去噪，解析出哪些 index 是最终获取的 target index <br>
     *
     * 算法藏在：http://www.99lib.net/command/99csw.js<br> 然后用js反混淆工具 decode：http://www.bm8.com.cn/jsConfusion/<br>
     *
     * @param metaContent 经过base64编码的content，被隐藏在 meta[4] 里
     */
    private static List<Integer> extractValidSectionIndexs(String metaContent) {

        byte[]        bts = Base64.decodeFast(metaContent);
        String        s   = new String(bts);
        List<Integer> e   = IntSplitter.split(s, "[A-Z]+%");

        Map<Integer, Integer> validSectionIndexMap = new TreeMap<>();

        int j    = 0;
        int star = 0;
        for (int i = 0; i < e.size(); i++) {
            if (e.get(i) < 3) {
                validSectionIndexMap.put(e.get(i), i + star);
                j++;
            } else {
                validSectionIndexMap.put(e.get(i) - j, i + star);
                j += 2;
            }
        }
        // 這裏竟然有重複的，去一下重
        ArrayList<Integer> validSectionIndexs = validSectionIndexMap.values().stream().distinct().collect(Collectors.toCollection(ArrayList::new));
        if (log.isDebugEnabled()) {
            log.debug(validSectionIndexMap.toString());
        }
        return validSectionIndexs;
    }

    /**
     * 用来去噪，获取出想要的 dom
     *
     * @param doms 待查找的 dom 集合
     * @param metaContent 经过base64编码的content，被隐藏在 meta[4] 里
     */
    public static <T> List<T> extractValidSections(List<T> doms, String metaContent) {
        int           size         = doms.size();
        List<Integer> indexes      = extractValidSectionIndexs(metaContent);
        List<T>       selectedDoms = new ArrayList<>();
        indexes.forEach(index -> {
            if (index < size) {
                selectedDoms.add(doms.get(index));
            }
        });
        return selectedDoms;
    }

}
