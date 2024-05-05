package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.config.SpiderConst;
import cn.cjp.spider.domain.CredentialDomain;
import cn.cjp.spider.domain.PipelineDomain;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MysqlPipeline extends AbstractPipeline {

    final CredentialDomain credential;

    public MysqlPipeline(PipelineDomain pipelineDomain, CredentialDomain credential) {
        super(pipelineDomain);
        this.credential = credential;
    }

    @Override
    @SneakyThrows
    public void process(ResultItems resultItems, Task task) {
        List<JSONObject> jsons = resultItems.get("jsons");
        if (jsons == null) {
            return;
        }

        jsons.stream().forEach(json -> {
            String             id         = json.getString("id");
            Set<String>        attrNames  = json.keySet();
            Collection<Object> attrValues = json.values();

            StringBuilder sql = new StringBuilder("insert into ");
            sql.append(json.getString(SpiderConst.KEY_TABLE_NAME));
            sql.append(attrNames.stream().collect(Collectors.joining(",", "(", ")")));
            sql.append(" values");
            sql.append(attrValues.stream().map(Objects::toString).collect(Collectors.joining(",", "(", ")")));

            try {
                MysqlDataSource ds = new MysqlDataSource();
                ds.setUrl(credential.getUrl());
                ds.setUser(credential.getUser());
                ds.setPassword(credential.getPass());
                ds.setUseSSL(credential.getSslEnable());

                try (Connection conn = ds.getConnection()) {
                    try (Statement stat = conn.createStatement()) {
                        int b = stat.executeUpdate(sql.toString());
                        log.debug("exec: " + sql + ", result: " + b);
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }


        });


    }
}
