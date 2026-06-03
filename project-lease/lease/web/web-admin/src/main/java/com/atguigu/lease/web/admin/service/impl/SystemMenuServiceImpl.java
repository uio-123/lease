package com.atguigu.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.lease.model.entity.SystemMenu;
import com.atguigu.lease.web.admin.mapper.SystemMenuMapper;
import com.atguigu.lease.web.admin.service.SystemMenuService;
import org.springframework.stereotype.Service;

@Service
public class SystemMenuServiceImpl extends ServiceImpl<SystemMenuMapper, SystemMenu>
        implements SystemMenuService {

}
