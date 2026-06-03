package com.atguigu.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.SystemRole;
import com.atguigu.lease.web.admin.mapper.SystemRoleMapper;
import com.atguigu.lease.web.admin.service.SystemRoleService;
import org.springframework.stereotype.Service;

@Service
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRole>
        implements SystemRoleService {

}
