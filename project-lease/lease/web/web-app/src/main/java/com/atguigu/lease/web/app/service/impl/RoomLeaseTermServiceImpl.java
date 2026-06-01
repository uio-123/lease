package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.model.entity.RoomLeaseTerm;
import com.atguigu.lease.web.app.mapper.RoomLeaseTermMapper;
import com.atguigu.lease.web.app.service.RoomLeaseTermService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author liubo
* @description 针对表【room_lease_term(房间租期管理表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
public class RoomLeaseTermServiceImpl extends ServiceImpl<RoomLeaseTermMapper, RoomLeaseTerm>
    implements RoomLeaseTermService{

    @Override
    public List<LeaseTerm> listByRoomId(Long roomId) {
        return baseMapper.selectListByRoomId(roomId);
    }
}




