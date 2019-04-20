package cn.cjp.spider.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Response<T> {

    int code;

    T data;

    String msg;

    public static final Response success() {
        return Response.builder().code(0).build();
    }

    public static final <D> Response success(D data) {
        return Response.builder().code(0).data(data).build();
    }

}
