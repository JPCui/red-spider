package cn.cjp;

import lombok.Data;

@Data
public class Server {

    public static class Mongo {
        public static final String host = "mongo.host";
        public static final int port = 27017;
    }

    @Data
    public static class Wechat {
        public static final String appid = "wx46b87cd60d921ad1";
        public static final String secret = "310318fe04525556d42ed82ef84ee607";
    }

}
