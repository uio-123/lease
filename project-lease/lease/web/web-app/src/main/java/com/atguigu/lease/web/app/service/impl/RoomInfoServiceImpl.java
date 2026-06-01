package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private RoomLabelMapper roomLabelMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private RoomFacilityMapper roomFacilityMapper;

    @Autowired
    private RoomPaymentTypeMapper roomPaymentTypeMapper;

    @Autowired
    private RoomLeaseTermMapper roomLeaseTermMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private RoomAttrValueMapper roomAttrValueMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private ApartmentLabelMapper apartmentLabelMapper;

    @Override
    public RoomDetailVo getDetailById(Long id) {
        // 1. 查询房间基本信息
        RoomInfo roomInfo = this.getById(id);
        if (roomInfo == null) {
            return null;
        }

        // 2. 查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());
        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        if (apartmentInfo != null) {
            BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);
            // 查询公寓图片
            List<GraphVo> apartmentGraphList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
            apartmentItemVo.setGraphVoList(apartmentGraphList);
            // 查询公寓标签
            List<LabelInfo> apartmentLabelList = apartmentLabelMapper.selectListByApartmentId(apartmentInfo.getId());
            apartmentItemVo.setLabelInfoList(apartmentLabelList);
        }

        // 3. 查询房间图片
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);

        // 4. 查询房间属性
        List<AttrValueVo> attrValueVoList = roomAttrValueMapper.selectListByRoomId(id);

        // 5. 查询房间配套
        List<FacilityInfo> facilityInfoList = roomFacilityMapper.selectListByRoomId(id);

        // 6. 查询房间标签
        List<LabelInfo> labelInfoList = roomLabelMapper.selectListByRoomId(id);

        // 7. 查询支付方式
        List<PaymentType> paymentTypeList = roomPaymentTypeMapper.selectListByRoomId(id);

        // 8. 查询杂费
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(roomInfo.getApartmentId());

        // 9. 查询租期
        List<LeaseTerm> leaseTermList = roomLeaseTermMapper.selectListByRoomId(id);

        // 10. 组装结果
        RoomDetailVo detailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, detailVo);
        detailVo.setApartmentItemVo(apartmentItemVo);
        detailVo.setGraphVoList(graphVoList);
        detailVo.setAttrValueVoList(attrValueVoList);
        detailVo.setFacilityInfoList(facilityInfoList);
        detailVo.setLabelInfoList(labelInfoList);
        detailVo.setPaymentTypeList(paymentTypeList);
        detailVo.setFeeValueVoList(feeValueVoList);
        detailVo.setLeaseTermList(leaseTermList);

        return detailVo;
    }

    @Override
    public IPage<RoomItemVo> pageByQuery(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return baseMapper.selectPageByQuery(page, queryVo);
    }

    @Override
    public IPage<RoomItemVo> pageByApartmentId(Page<RoomItemVo> page, Long apartmentId) {
        return baseMapper.selectPageByApartmentId(page, apartmentId);
    }
}




