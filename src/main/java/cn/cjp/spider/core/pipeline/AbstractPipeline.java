package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.model.PipelineDomain;
import cn.hutool.extra.expression.engine.spel.SpELEngine;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.pipeline.Pipeline;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPipeline implements Pipeline {

    @Getter(AccessLevel.PROTECTED)
    final PipelineDomain pipelineDomain;

    protected String getValue(String exp, Map<String, Object> ctx) {
        Object eval = new SpELEngine().eval(exp, ctx);
        if (eval instanceof String) {
            return eval.toString();
        }

        log.warn("eval is not a string: " + eval);
        return "[object]";
    }


}
