package com.atguigu.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.SystemDept;
import com.atguigu.lease.web.admin.mapper.SystemDeptMapper;
import com.atguigu.lease.web.admin.service.SystemDeptService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SystemDeptServiceImpl extends ServiceImpl<SystemDeptMapper, SystemDept>
        implements SystemDeptService {

    @Override
    public List<SystemDept> listAsTree() {
        List<SystemDept> all = this.list();
        return buildTree(all, 0L);
    }

    private List<SystemDept> buildTree(List<SystemDept> all, Long parentId) {
        return all.stream()
                .filter(dept -> {
                    Long pid = dept.getParentId();
                    return pid != null && pid.equals(parentId);
                })
                .peek(dept -> dept.setChildren(buildTree(all, dept.getId())))
                .collect(Collectors.toList());
    }
}
