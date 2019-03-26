package cn.cjp;

import lombok.Data;

@Data
public class Server {

    public static class Mongo {

        public static final String host = "mongo.host";
        public static final int    port = 27017;
    }

}
