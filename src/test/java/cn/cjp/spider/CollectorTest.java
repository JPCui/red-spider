package cn.cjp.spider;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectorTest {

    public static void main(String[] args) {
        List<Data> list = new ArrayList<>();
        list.add(new Data(1, 1, 10));
        list.add(new Data(2, 2, 11));
        list.add(new Data(1, 2, 11));

        Map<Integer, Data> map = list.stream().collect(Collectors.toMap(Data::getId, Function.identity(), (d1, d2) -> {
            if (d1.startTime > d2.startTime) {//选择开始时间靠后的记录
                return d1;
            }
            return d2;
        }));

        System.out.println(map);
    }

    @lombok.Data
    @Getter
    public static class Data {

        public Data(int id, int startTime, int endTime) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public int id;

        public int startTime;

        public int endTime;

    }
}

