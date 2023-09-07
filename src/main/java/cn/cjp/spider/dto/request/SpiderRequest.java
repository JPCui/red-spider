package cn.cjp.spider.dto.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpiderRequest {

    // TODO 以后可以考虑分布式，增加分片功能
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class ConfigRequest {

        @NotEmpty
        String siteName;

        // 分片数
//        int replicaNum;
        // 核心线程数
        int threadCoreNum = Runtime.getRuntime().availableProcessors();
        // 最大线程数
        int threadMaxNum  = Runtime.getRuntime().availableProcessors() * 2;
        @ApiModelProperty(value = "超时时间，ms")
        int timeout = 30_000;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class StartRequest {

        @NotEmpty
        String siteName;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class StopRequest {

        @NotEmpty
        String siteName;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class RestartRequest {

        @NotEmpty
        String siteName;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class RunRequest {

        @NotEmpty
        String url;
    }

}
