package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.BrowsingHistoryService;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.atguigu.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Override
    public IPage<HistoryItemVo> pageByUserId(Page<HistoryItemVo> page, Long userId) {
        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getUserId, userId);
        queryWrapper.orderByDesc(BrowsingHistory::getBrowseTime);
        Page<BrowsingHistory> historyPage = this.page(new Page<>(1, 100));

        List<HistoryItemVo> resultList = historyPage.getRecords().stream().map(history -> {
            HistoryItemVo itemVo = new HistoryItemVo();
            itemVo.setId(history.getId());
            itemVo.setUserId(history.getUserId());
            itemVo.setRoomId(history.getRoomId());
            itemVo.setBrowseTime(history.getBrowseTime());

            RoomInfo roomInfo = roomInfoMapper.selectById(history.getRoomId());
            if (roomInfo != null) {
                itemVo.setRoomNumber(roomInfo.getRoomNumber());
                itemVo.setRent(roomInfo.getRent());

                List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, roomInfo.getId());
                itemVo.setRoomGraphVoList(graphVoList);

                ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());
                if (apartmentInfo != null) {
                    itemVo.setApartmentName(apartmentInfo.getName());
                    itemVo.setProvinceName(apartmentInfo.getProvinceName());
                    itemVo.setCityName(apartmentInfo.getCityName());
                    itemVo.setDistrictName(apartmentInfo.getDistrictName());
                }
            }

            return itemVo;
        }).toList();

        Page<HistoryItemVo> resultPage = new Page<>(page.getCurrent(), page.getSize(), historyPage.getTotal());
        resultPage.setRecords(resultList);
        return resultPage;
    }

    @Override
    public void saveBrowsingHistory(Long userId, Long roomId) {
        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getUserId, userId);
        queryWrapper.eq(BrowsingHistory::getRoomId, roomId);
        BrowsingHistory existingHistory = this.getOne(queryWrapper);

        if (existingHistory != null) {
            LambdaUpdateWrapper<BrowsingHistory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(BrowsingHistory::getId, existingHistory.getId());
            updateWrapper.set(BrowsingHistory::getBrowseTime, new Date());
            this.update(updateWrapper);
        } else {
            BrowsingHistory history = new BrowsingHistory();
            history.setUserId(userId);
            history.setRoomId(roomId);
            history.setBrowseTime(new Date());
            this.save(history);
        }
    }

    @Override
    public void deleteById(Long id, Long userId) {
        BrowsingHistory history = this.getById(id);
        if (history == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        if (!history.getUserId().equals(userId)) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_AUTH);
        }

        this.removeById(id);
    }
}