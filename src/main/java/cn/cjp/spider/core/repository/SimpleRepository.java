package cn.cjp.spider.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.alibaba.fastjson.JSONObject;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SimpleRepository implements Pipeline, AbstractRepository {

    private static final Map<String, Queue<String>> repo = new ConcurrentHashMap<>();

    public void put(String key, Queue<String> value) {
        repo.put(key, value);
    }

    public Queue<String> get(String key) {
        return repo.get(key);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        JSONObject json = resultItems.get("json");
        if (json != null) {
            this.push(json.getString("db"), json.toJSONString());
        }
        List<JSONObject> jsons = resultItems.get("jsons");
        if (jsons != null) {
            jsons.stream().forEach(j -> {
                this.push(j.getString("db"), j.toJSONString());
            });
        }
    }

    @Override
    public void push(String key, String value) {
        Queue<String> list = repo.get(key);
        if (list == null) {
            initList(key);
        }

        list.add(value);
    }

    @Override
    public String pop(String key) {
        return repo.get(key).poll();
    }

    private void initList(String key) {
        Queue<String> list = repo.get(key);
        if (list == null) {
            synchronized (list) {
                if (list == null) {
                    list = new LinkedBlockingQueue<>();
                    repo.put(key, list);
                }
            }
        }
    }

}
