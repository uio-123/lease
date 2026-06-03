package com.atguigu.lease.web.admin.controller.system;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemRole;
import com.atguigu.lease.model.entity.SystemRoleMenu;
import com.atguigu.lease.web.admin.mapper.SystemRoleMenuMapper;
import com.atguigu.lease.web.admin.service.SystemRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "后台角色管理")
@RestController
@RequestMapping("/admin/system")
public class SystemRoleController {

    @Autowired
    private SystemRoleService roleService;

    @Autowired
    private SystemRoleMenuMapper roleMenuMapper;

    @Operation(summary = "查询所有角色")
    @GetMapping("sysRole/findAll")
    public Result<List<SystemRole>> findAll() {
        List<SystemRole> list = roleService.list();
        return Result.ok(list);
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("role/page")
    public Result<IPage<SystemRole>> page(@RequestParam long current, @RequestParam long size) {
        IPage<SystemRole> page = new Page<>(current, size);
        IPage<SystemRole> result = roleService.page(page);
        return Result.ok(result);
    }

    @Operation(summary = "保存或更新角色")
    @PostMapping("role/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemRole role) {
        roleService.saveOrUpdate(role);
        return Result.ok();
    }

    @Operation(summary = "根据id删除角色")
    @DeleteMapping("role/removeById")
    public Result removeById(@RequestParam Long id) {
        roleService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("sysRole/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        roleService.removeByIds(idList);
        return Result.ok();
    }

    @Operation(summary = "给角色分配菜单权限")
    @PostMapping("role/updateMenuListById")
    public Result updateMenuListById(@RequestParam Long id, @RequestBody List<Long> menuIdList) {
        // 删除角色原有的菜单关联
        LambdaQueryWrapper<SystemRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemRoleMenu::getRoleId, id);
        roleMenuMapper.delete(queryWrapper);

        // 插入新的菜单关联
        List<SystemRoleMenu> roleMenus = menuIdList.stream().map(menuId -> {
            SystemRoleMenu roleMenu = new SystemRoleMenu();
            roleMenu.setRoleId(id);
            roleMenu.setMenuId(menuId);
            return roleMenu;
        }).collect(Collectors.toList());

        if (!roleMenus.isEmpty()) {
            roleMenus.forEach(roleMenuMapper::insert);
        }

        return Result.ok();
    }
}
