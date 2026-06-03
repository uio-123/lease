package com.atguigu.lease.web.admin.service;

import com.atguigu.lease.model.entity.SystemDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SystemDeptService extends IService<SystemDept> {

    List<SystemDept> listAsTree();
}
