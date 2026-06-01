package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.model.enums.AppointmentStatus;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.app.mapper.GraphInfoMapper;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Override
    public void saveAppointment(ViewAppointment viewAppointment) {
        // 设置用户ID
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }
        Long userId = loginUser.getUserId();
        viewAppointment.setUserId(userId);

        // 设置默认状态为待看房
        if (viewAppointment.getAppointmentStatus() == null) {
            viewAppointment.setAppointmentStatus(AppointmentStatus.WAITING);
        }

        this.save(viewAppointment);
    }

    @Override
    public List<AppointmentItemVo> listAppointmentsByUserId(Long userId) {
        LambdaQueryWrapper<ViewAppointment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ViewAppointment::getUserId, userId);
        queryWrapper.orderByDesc(ViewAppointment::getCreateTime);
        List<ViewAppointment> list = this.list(queryWrapper);

        return list.stream().map(appointment -> {
            AppointmentItemVo itemVo = new AppointmentItemVo();
            itemVo.setId(appointment.getId());
            itemVo.setAppointmentTime(appointment.getAppointmentTime());
            itemVo.setAppointmentStatus(appointment.getAppointmentStatus());

            // 查询公寓信息
            ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(appointment.getApartmentId());
            if (apartmentInfo != null) {
                itemVo.setApartmentName(apartmentInfo.getName());
                // 查询公寓图片
                List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
                itemVo.setGraphVoList(graphVoList);
            }

            return itemVo;
        }).toList();
    }

    @Override
    public AppointmentDetailVo getAppointmentDetailById(Long id, Long userId) {
        ViewAppointment appointment = this.getById(id);
        if (appointment == null) {
            return null;
        }

        // 校验是否是当前用户的预约
        if (!appointment.getUserId().equals(userId)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        AppointmentDetailVo detailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(appointment, detailVo);

        // 查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(appointment.getApartmentId());
        if (apartmentInfo != null) {
            ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
            BeanUtils.copyProperties(apartmentInfo, apartmentItemVo);
            // 查询公寓图片
            List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, apartmentInfo.getId());
            apartmentItemVo.setGraphVoList(graphVoList);
            detailVo.setApartmentItemVo(apartmentItemVo);
        }

        return detailVo;
    }
}




