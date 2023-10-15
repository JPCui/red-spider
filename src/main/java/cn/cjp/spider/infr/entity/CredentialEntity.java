package cn.cjp.spider.infr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@TableName("credential")
public class CredentialEntity {

    @TableId(type = IdType.ASSIGN_ID)
    String id;
    String type;
    /**
     * 所属用户ID
     */
    String userId;
    String config;

}
