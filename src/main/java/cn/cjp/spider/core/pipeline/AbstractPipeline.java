package cn.cjp.spider.core.pipeline;

import cn.cjp.spider.core.model.PipelineDomain;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import us.codecraft.webmagic.pipeline.Pipeline;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public abstract class AbstractPipeline implements Pipeline {

    final PipelineDomain pipelineDomain;




}
