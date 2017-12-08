package cn.cjp.spider.core.repository;

import java.util.Queue;

public interface AbstractRepository {

    public void put(String key, Queue<String> value);

    public Queue<String> get(String key);
    
    public void push(String key, String value);
    
    public String pop(String key);
}
