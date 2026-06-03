package com.atguigu.lease.model.entity;

import com.atguigu.lease.model.enums.BaseStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "角色信息")
@TableName(value = "system_role")
@Data
public class SystemRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "角色编码")
    @TableField(value = "code")
    private String code;

    @Schema(description = "角色描述")
    @TableField(value = "description")
    private String description;

    @Schema(description = "角色状态")
    @TableField(value = "status")
    private BaseStatus status;

    @Schema(description = "是否已分配给当前用户（非数据库字段）")
    @TableField(exist = false)
    private Boolean selected;
}
