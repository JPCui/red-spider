package cn.cjp.spider.core.model;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class PipelineDomain {

    String  id;
    Boolean enabled = false;
    Params  params;

    @JsonIgnoreProperties(ignoreUnknown = true)
    // fastjson
    @JSONType(seeAlso = {EmailParams.class, SimpleParams.class})
    // jackson
    @JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = EmailParams.class, name = "email"),
        @JsonSubTypes.Type(value = SimpleParams.class, name = "simple"),
    })
    public interface Params {

    }

    // fastjson
    @JSONType(typeName = "simple")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class SimpleParams implements Params {
        String s = "";
    }

    // fastjson
    @JSONType(typeName = "email")
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    public static class EmailParams implements Params {

        List<String> to;
        String       title;
        String       content;

    }

}
