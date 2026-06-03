package com.atguigu.lease.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "角色菜单关联")
@TableName(value = "system_role_menu")
@Data
public class SystemRoleMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色id")
    @TableField(value = "role_id")
    private Long roleId;

    @Schema(description = "菜单id")
    @TableField(value = "menu_id")
    private Long menuId;
}
