package cn.cjp.app.model.response;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ChaptorsResponse implements Serializable {

    private String _id;

    List<ChaptorResponse> chaptors;

    /**
     * 抓取的ID不能暴露
     *
     * @author sucre
     */
    @Data
    public static class ChaptorResponse implements Serializable {

        private String chaptorName;

        private String viewId;
    }
}
