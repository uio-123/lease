package com.atguigu.lease.web.admin.controller.system;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemMenu;
import com.atguigu.lease.web.admin.mapper.SystemRoleMenuMapper;
import com.atguigu.lease.web.admin.service.SystemMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "后台菜单管理")
@RestController
@RequestMapping("/admin/system/menu")
public class SystemMenuController {

    @Autowired
    private SystemMenuService menuService;

    @Autowired
    private SystemRoleMenuMapper roleMenuMapper;

    @Operation(summary = "获取菜单树形列表")
    @GetMapping("listAsTree")
    public Result<List<SystemMenu>> listAsTree() {
        List<SystemMenu> allMenus = menuService.list();
        List<SystemMenu> tree = buildMenuTree(allMenus, 0L);
        return Result.ok(tree);
    }

    @Operation(summary = "根据角色id获取已分配的菜单id列表")
    @GetMapping("listAsTreeByRoleId")
    public Result<List<Long>> listAsTreeByRoleId(@RequestParam Long id) {
        LambdaQueryWrapper<com.atguigu.lease.model.entity.SystemRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(com.atguigu.lease.model.entity.SystemRoleMenu::getRoleId, id);
        List<com.atguigu.lease.model.entity.SystemRoleMenu> roleMenus = roleMenuMapper.selectList(queryWrapper);
        List<Long> menuIds = roleMenus.stream()
                .map(com.atguigu.lease.model.entity.SystemRoleMenu::getMenuId)
                .collect(Collectors.toList());
        return Result.ok(menuIds);
    }

    @Operation(summary = "保存或更新菜单")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SystemMenu menu) {
        menuService.saveOrUpdate(menu);
        return Result.ok();
    }

    @Operation(summary = "根据id删除菜单")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        menuService.removeById(id);
        return Result.ok();
    }

    private List<SystemMenu> buildMenuTree(List<SystemMenu> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> {
                    Long pid = menu.getParentId();
                    return pid != null && pid.equals(parentId);
                })
                .peek(menu -> menu.setChildren(buildMenuTree(allMenus, menu.getId())))
                .collect(Collectors.toList());
    }
}
