package cn.cjp.spider.domain;

import cn.cjp.spider.infr.entity.CredentialEntity;
import cn.cjp.spider.infr.mapper.CredentialMapper;
import cn.cjp.spider.infr.util.CredentialType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CredentialRepo {

    final CredentialMapper credentialMapper;
    final ObjectMapper     objectMapper;

    @SneakyThrows
    public CredentialDomain getOrDefaultByUserId(String userId, String id) {
        LambdaQueryWrapper<CredentialEntity> q = Wrappers.lambdaQuery(CredentialEntity.class)
            .eq(CredentialEntity::getUserId, userId)
            .eq(StringUtils.isNotBlank(id), CredentialEntity::getId, id);
        q.last("limit 1");
        CredentialEntity entity = credentialMapper.selectOne(q);
        if (entity == null) {
            return null;
        }

        CredentialDomain domain = objectMapper.readValue(entity.getConfig(), CredentialDomain.class);
        domain.id = entity.getId();
        domain.userId = entity.getUserId();
        domain.type = CredentialType.valueOf(entity.getType());
        return domain;
    }

}
