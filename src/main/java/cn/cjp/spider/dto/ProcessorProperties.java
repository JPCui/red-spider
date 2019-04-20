package cn.cjp.spider.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ProcessorProperties {

    int threadNum = 1;

    int timeout = 30_000;

    int sleepTime = 1000;

    /**
     * 重试时的间隔时间
     *
     * TODO 可以优化为重试策略
     */
    int retrySleepTime = 3_000;

    int retryTimes = 3;


}
