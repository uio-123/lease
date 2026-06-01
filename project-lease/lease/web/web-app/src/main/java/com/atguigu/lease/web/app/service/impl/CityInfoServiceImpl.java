package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.CityInfo;
import com.atguigu.lease.web.app.mapper.CityInfoMapper;
import com.atguigu.lease.web.app.service.CityInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【city_info】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class CityInfoServiceImpl extends ServiceImpl<CityInfoMapper, CityInfo>
    implements CityInfoService{

    @Override
    public List<CityInfo> listByProvinceId(Long provinceId) {
        LambdaQueryWrapper<CityInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CityInfo::getProvinceId, provinceId);
        return this.list(queryWrapper);
    }
}




