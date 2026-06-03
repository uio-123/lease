package com.atguigu.lease.web.admin.controller.system;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.SystemDept;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.service.SystemDeptService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台部门管理")
@RestController
@RequestMapping("/admin/system/sysDept")
public class SystemDeptController {

    @Autowired
    private SystemDeptService deptService;

    @Operation(summary = "获取部门树形列表")
    @GetMapping("findNodes")
    public Result<List<SystemDept>> findNodes() {
        List<SystemDept> tree = deptService.listAsTree();
        return Result.ok(tree);
    }

    @Operation(summary = "更新部门状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable BaseStatus status) {
        LambdaUpdateWrapper<SystemDept> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SystemDept::getId, id);
        updateWrapper.set(SystemDept::getStatus, status);
        deptService.update(updateWrapper);
        return Result.ok();
    }

    @Operation(summary = "保存部门")
    @PostMapping("save")
    public Result save(@RequestBody SystemDept dept) {
        deptService.save(dept);
        return Result.ok();
    }

    @Operation(summary = "更新部门")
    @PutMapping("update")
    public Result update(@RequestBody SystemDept dept) {
        deptService.updateById(dept);
        return Result.ok();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        deptService.removeById(id);
        return Result.ok();
    }
}
