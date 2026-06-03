package com.atguigu.lease.web.app.vo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "省市区树形结构")
public class RegionTreeVo {

    @Schema(description = "地区id")
    private Long id;

    @Schema(description = "地区名称")
    private String name;

    @Schema(description = "子级地区列表（市级或区县级）")
    private List<RegionTreeVo> children;
}
