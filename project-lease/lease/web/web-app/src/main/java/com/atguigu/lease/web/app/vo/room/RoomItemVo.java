package com.atguigu.lease.web.app.vo.room;


import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "APP房间列表实体")
@Data
public class RoomItemVo {

    @Schema(description = "房间id")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租金（元/月）")
    private BigDecimal rent;

    @Schema(description = "所属公寓id")
    private Long apartmentId;

    @Schema(description = "所属公寓名称")
    private String apartmentName;

    @Schema(description = "所处区域id")
    private Long districtId;

    @Schema(description = "所处区域名称")
    private String districtName;

    @Schema(description = "所处城市id")
    private Long cityId;

    @Schema(description = "所处城市名称")
    private String cityName;

    @Schema(description = "所处省份id")
    private Long provinceId;

    @Schema(description = "所处省份名称")
    private String provinceName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "经度")
    private String latitude;

    @Schema(description = "纬度")
    private String longitude;

    @Schema(description = "房间图片列表")
    private List<GraphVo> graphVoList;

    @Schema(description = "房间标签列表")
    private List<LabelInfo> labelInfoList;
}
