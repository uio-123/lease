package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.DistrictInfo;
import com.atguigu.lease.web.app.mapper.DistrictInfoMapper;
import com.atguigu.lease.web.app.service.DistrictInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【district_info】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class DistrictInfoServiceImpl extends ServiceImpl<DistrictInfoMapper, DistrictInfo>
    implements DistrictInfoService{

    @Override
    public List<DistrictInfo> listByCityId(Long cityId) {
        LambdaQueryWrapper<DistrictInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DistrictInfo::getCityId, cityId);
        return this.list(queryWrapper);
    }
}




