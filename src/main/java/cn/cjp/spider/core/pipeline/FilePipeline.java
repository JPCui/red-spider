package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.config.SpiderConst;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.FilePersistentBase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class FilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public FilePipeline() {
        setPath("/data/webmagic/");
    }

    public FilePipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.get("jsons") != null) {
            List<JSONObject> jsons = resultItems.get("jsons");
            jsons.forEach(json -> this.process(json));
        } else if (resultItems.get("json") != null) {
            JSONObject json = resultItems.get("json");
            this.process(json);
        }
    }

    private void process(JSONObject json) {
        String path = this.path + PATH_SEPERATOR + json.get(SpiderConst.KEY_DB_NAME) + PATH_SEPERATOR + json.get(SpiderConst.KEY_TABLE_NAME) + ".txt";
        try {
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path), true), "UTF-8"));
            printWriter.println("url:\t" + json.get(SpiderConst.KEY_REFER_URL));
            json.forEach((key, value) -> {
                printWriter.print(key);
                printWriter.print(":\t");
                if (value != null) {
                    if (value.getClass().isArray()) {
                        Object[] vs = (Object[]) value;
                        if (vs.length > 0) {
                            printWriter.print(vs[0]);
                            for (int i = 1; i < vs.length; i++) {
                                printWriter.print(", ");
                                printWriter.print(vs[i]);
                            }
                        }
                    } else if (value instanceof Iterable) {
                        Iterator valueIt = ((Iterable) value).iterator();
                        if (valueIt.hasNext()) {
                            printWriter.print(valueIt.next());
                            while (valueIt.hasNext()) {
                                printWriter.print(valueIt.next());
                            }
                        }
                    } else {
                        printWriter.print(value);
                    }
                }
                printWriter.println("");
            });
            printWriter.println("");
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
