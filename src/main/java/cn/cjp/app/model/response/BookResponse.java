package cn.cjp.app.model.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class BookResponse implements Serializable {

    private String _id;

    private String name;

    private String author;

    private String type;

    private String[] tags;

    private String summary;

}
