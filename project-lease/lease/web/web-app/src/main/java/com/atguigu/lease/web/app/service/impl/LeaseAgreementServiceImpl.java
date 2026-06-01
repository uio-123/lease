package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Override
    public List<AgreementItemVo> listAgreementsByUserId(Long userId) {
        LambdaQueryWrapper<LeaseAgreement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseAgreement::getPhone, userId.toString());
        queryWrapper.orderByDesc(LeaseAgreement::getCreateTime);
        List<LeaseAgreement> list = this.list(queryWrapper);

        return list.stream().map(agreement -> {
            AgreementItemVo itemVo = new AgreementItemVo();
            itemVo.setId(agreement.getId());
            itemVo.setLeaseStatus(agreement.getStatus());
            itemVo.setLeaseStartDate(agreement.getLeaseStartDate());
            itemVo.setLeaseEndDate(agreement.getLeaseEndDate());
            itemVo.setSourceType(agreement.getSourceType());
            itemVo.setRent(agreement.getRent());
            itemVo.setRoomNumber(agreement.getRoomId() != null ?
                    roomInfoMapper.selectById(agreement.getRoomId()).getRoomNumber() : null);

            // 查询公寓名称
            if (agreement.getApartmentId() != null) {
                ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(agreement.getApartmentId());
                if (apartmentInfo != null) {
                    itemVo.setApartmentName(apartmentInfo.getName());
                }
            }

            // 查询房间图片
            if (agreement.getRoomId() != null) {
                List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, agreement.getRoomId());
                itemVo.setRoomGraphVoList(graphVoList);
            }

            return itemVo;
        }).toList();
    }

    @Override
    public AgreementDetailVo getAgreementDetailById(Long id, Long userId) {
        LeaseAgreement agreement = this.getById(id);
        if (agreement == null) {
            return null;
        }

        // 校验是否是当前用户的租约
        if (!StringUtils.hasText(agreement.getPhone()) ||
                !agreement.getPhone().equals(userId.toString())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        AgreementDetailVo detailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(agreement, detailVo);

        // 查询公寓信息
        if (agreement.getApartmentId() != null) {
            ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(agreement.getApartmentId());
            if (apartmentInfo != null) {
                detailVo.setApartmentName(apartmentInfo.getName());
                // 查询公寓图片
                List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
                detailVo.setApartmentGraphVoList(apartmentGraphVoList);
            }
        }

        // 查询房间信息
        if (agreement.getRoomId() != null) {
            RoomInfo roomInfo = roomInfoMapper.selectById(agreement.getRoomId());
            if (roomInfo != null) {
                detailVo.setRoomNumber(roomInfo.getRoomNumber());
            }
            // 查询房间图片
            List<GraphVo> roomGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, agreement.getRoomId());
            detailVo.setRoomGraphVoList(roomGraphVoList);
        }

        // 查询支付方式
        if (agreement.getPaymentTypeId() != null) {
            PaymentType paymentType = paymentTypeMapper.selectById(agreement.getPaymentTypeId());
            if (paymentType != null) {
                detailVo.setPaymentTypeName(paymentType.getName());
            }
        }

        // 查询租期
        if (agreement.getLeaseTermId() != null) {
            LeaseTerm leaseTerm = leaseTermMapper.selectById(agreement.getLeaseTermId());
            if (leaseTerm != null) {
                detailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
                detailVo.setLeaseTermUnit(leaseTerm.getUnit());
            }
        }

        return detailVo;
    }

    @Override
    public void updateStatusById(Long id, LeaseStatus leaseStatus, Long userId) {
        LeaseAgreement agreement = this.getById(id);
        if (agreement == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        // 校验是否是当前用户的租约
        if (!StringUtils.hasText(agreement.getPhone()) ||
                !agreement.getPhone().equals(userId.toString())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        agreement.setStatus(leaseStatus);
        this.updateById(agreement);
    }

    @Override
    public void saveOrUpdateAgreement(LeaseAgreement leaseAgreement, Long userId) {
        // 设置用户手机号
        leaseAgreement.setPhone(userId.toString());

        // 如果是新增，设置初始状态
        if (leaseAgreement.getId() == null) {
            leaseAgreement.setStatus(LeaseStatus.SIGNING);
        }

        this.saveOrUpdate(leaseAgreement);
    }
}




