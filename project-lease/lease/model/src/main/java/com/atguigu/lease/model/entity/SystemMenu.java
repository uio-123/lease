package com.atguigu.lease.model.entity;

import com.atguigu.lease.model.enums.BaseStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "菜单权限信息")
@TableName(value = "system_menu")
@Data
public class SystemMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "父菜单id")
    @TableField(value = "parent_id")
    private Long parentId;

    @Schema(description = "菜单名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "菜单类型（0目录 1菜单 2按钮）")
    @TableField(value = "type")
    private Integer type;

    @Schema(description = "路由路径")
    @TableField(value = "path")
    private String path;

    @Schema(description = "组件路径")
    @TableField(value = "component")
    private String component;

    @Schema(description = "权限标识")
    @TableField(value = "perms")
    private String perms;

    @Schema(description = "菜单图标")
    @TableField(value = "icon")
    private String icon;

    @Schema(description = "排序")
    @TableField(value = "sort_value")
    private Integer sortValue;

    @Schema(description = "菜单状态")
    @TableField(value = "status")
    private BaseStatus status;

    @Schema(description = "是否隐藏（0否 1是）")
    @TableField(value = "is_hide")
    private Integer isHide;

    @Schema(description = "子菜单列表")
    @TableField(exist = false)
    private List<SystemMenu> children;
}
