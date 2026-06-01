package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.ReleaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        // 1. 查询公寓基本信息
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null) {
            return null;
        }

        // 2. 查询图片列表
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        // 3. 查询标签列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        // 4. 查询配套列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);

        // 5. 查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);

        // 6. 查询最低租金
        BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(id);

        // 7. 组装结果
        ApartmentDetailVo detailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, detailVo);
        detailVo.setGraphVoList(graphVoList);
        detailVo.setLabelInfoList(labelInfoList);
        detailVo.setFacilityInfoList(facilityInfoList);
        detailVo.setFeeValueVoList(feeValueVoList);
        detailVo.setMinRent(minRent);

        return detailVo;
    }

    @Override
    public List<ApartmentItemVo> listByDistrictId(Long districtId) {
        LambdaQueryWrapper<ApartmentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApartmentInfo::getDistrictId, districtId);
        queryWrapper.eq(ApartmentInfo::getIsRelease, ReleaseStatus.RELEASED);
        List<ApartmentInfo> list = this.list(queryWrapper);

        return list.stream().map(apartmentInfo -> {
            ApartmentItemVo itemVo = new ApartmentItemVo();
            BeanUtils.copyProperties(apartmentInfo, itemVo);

            // 查询图片
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
            itemVo.setGraphVoList(graphVoList);

            // 查询标签
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(apartmentInfo.getId());
            itemVo.setLabelInfoList(labelInfoList);

            // 查询最低租金
            BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(apartmentInfo.getId());
            itemVo.setMinRent(minRent);

            return itemVo;
        }).toList();
    }

    @Override
    public List<ApartmentItemVo> listByQuery(Long provinceId, Long cityId, Long districtId) {
        LambdaQueryWrapper<ApartmentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(provinceId != null, ApartmentInfo::getProvinceId, provinceId);
        queryWrapper.eq(cityId != null, ApartmentInfo::getCityId, cityId);
        queryWrapper.eq(districtId != null, ApartmentInfo::getDistrictId, districtId);
        queryWrapper.eq(ApartmentInfo::getIsRelease, ReleaseStatus.RELEASED);
        List<ApartmentInfo> list = this.list(queryWrapper);

        return list.stream().map(apartmentInfo -> {
            ApartmentItemVo itemVo = new ApartmentItemVo();
            BeanUtils.copyProperties(apartmentInfo, itemVo);

            // 查询图片
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
            itemVo.setGraphVoList(graphVoList);

            // 查询标签
            List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(apartmentInfo.getId());
            itemVo.setLabelInfoList(labelInfoList);

            // 查询最低租金
            BigDecimal minRent = roomInfoMapper.selectMinRentByApartmentId(apartmentInfo.getId());
            itemVo.setMinRent(minRent);

            return itemVo;
        }).toList();
    }
}




