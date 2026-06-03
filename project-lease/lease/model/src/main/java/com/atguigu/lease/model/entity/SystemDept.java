package com.atguigu.lease.model.entity;

import com.atguigu.lease.model.enums.BaseStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "部门信息")
@TableName(value = "system_dept")
@Data
public class SystemDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "部门名称")
    @TableField(value = "name")
    private String name;

    @Schema(description = "父部门id")
    @TableField(value = "parent_id")
    private Long parentId;

    @Schema(description = "树路径")
    @TableField(value = "tree_path")
    private String treePath;

    @Schema(description = "排序")
    @TableField(value = "sort_value")
    private Integer sortValue;

    @Schema(description = "负责人")
    @TableField(value = "leader")
    private String leader;

    @Schema(description = "联系电话")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "部门状态")
    @TableField(value = "status")
    private BaseStatus status;

    @Schema(description = "子部门列表")
    @TableField(exist = false)
    private List<SystemDept> children;
}
