package com.atguigu.lease.web.admin.controller.system;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemRole;
import com.atguigu.lease.model.entity.SystemUserRole;
import com.atguigu.lease.web.admin.mapper.SystemUserRoleMapper;
import com.atguigu.lease.web.admin.service.SystemRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "后台系统用户角色管理")
@RestController
@RequestMapping("/admin/system/user")
public class SystemUserRoleController {

    @Autowired
    private SystemRoleService roleService;

    @Autowired
    private SystemUserRoleMapper userRoleMapper;

    @Operation(summary = "根据用户id查询角色列表（含 selected 标记）")
    @GetMapping("listRoleByUserId")
    public Result<List<SystemRole>> listRoleByUserId(@RequestParam Long id) {
        // 查询所有角色
        List<SystemRole> allRoles = roleService.list();

        // 查询用户已分配的角色ID
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemUserRole> queryWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(SystemUserRole::getUserId, id);
        List<SystemUserRole> userRoles = userRoleMapper.selectList(queryWrapper);
        Set<Long> assignedRoleIds = userRoles.stream()
                .map(SystemUserRole::getRoleId)
                .collect(Collectors.toSet());

        // 给每个角色设置 selected 标记
        allRoles.forEach(role -> role.setSelected(assignedRoleIds.contains(role.getId())));

        return Result.ok(allRoles);
    }

    @Operation(summary = "分配角色给用户")
    @PostMapping("updateRoleListById")
    public Result updateRoleListById(@RequestParam Long id, @RequestBody List<Long> roleIdList) {
        // 删除用户原有的角色关联
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemUserRole> queryWrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(SystemUserRole::getUserId, id);
        userRoleMapper.delete(queryWrapper);

        // 插入新的角色关联
        List<SystemUserRole> userRoles = roleIdList.stream().map(roleId -> {
            SystemUserRole userRole = new SystemUserRole();
            userRole.setUserId(id);
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());

        if (!userRoles.isEmpty()) {
            userRoles.forEach(userRoleMapper::insert);
        }

        return Result.ok();
    }
}
