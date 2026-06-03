package com.atguigu.lease.web.app.controller.region;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.CityInfo;
import com.atguigu.lease.model.entity.DistrictInfo;
import com.atguigu.lease.model.entity.ProvinceInfo;
import com.atguigu.lease.web.app.service.CityInfoService;
import com.atguigu.lease.web.app.service.DistrictInfoService;
import com.atguigu.lease.web.app.service.ProvinceInfoService;
import com.atguigu.lease.web.app.vo.region.RegionTreeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "地区信息")
@RestController
@RequestMapping("/app/region")
public class RegionController {

    @Autowired
    private ProvinceInfoService provinceInfoService;

    @Autowired
    private CityInfoService cityInfoService;

    @Autowired
    private DistrictInfoService districtInfoService;

    @Operation(summary = "查询省份信息列表")
    @GetMapping("province/list")
    public Result<List<ProvinceInfo>> listProvince() {
        List<ProvinceInfo> list = provinceInfoService.list();
        return Result.ok(list);
    }

    @Operation(summary = "根据省份id查询城市信息列表")
    @GetMapping("city/listByProvinceId")
    public Result<List<CityInfo>> listCityInfoByProvinceId(@RequestParam Long id) {
        List<CityInfo> list = cityInfoService.listByProvinceId(id);
        return Result.ok(list);
    }

    @GetMapping("district/listByCityId")
    @Operation(summary = "根据城市id查询区县信息")
    public Result<List<DistrictInfo>> listDistrictInfoByCityId(@RequestParam Long id) {
        List<DistrictInfo> list = districtInfoService.listByCityId(id);
        return Result.ok(list);
    }

    @Operation(summary = "查询省市区树形结构列表")
    @GetMapping("listAsTree")
    public Result<List<RegionTreeVo>> listAsTree() {
        List<ProvinceInfo> provinceList = provinceInfoService.list();
        List<CityInfo> cityList = cityInfoService.list();
        List<DistrictInfo> districtList = districtInfoService.list();

        List<RegionTreeVo> tree = provinceList.stream().map(province -> {
            RegionTreeVo provinceNode = new RegionTreeVo(province.getId(), province.getName(), null);

            List<RegionTreeVo> cityNodes = cityList.stream()
                    .filter(city -> city.getProvinceId().equals(province.getId().intValue()))
                    .map(city -> {
                        List<RegionTreeVo> districtNodes = districtList.stream()
                                .filter(district -> district.getCityId().equals(city.getId().intValue()))
                                .map(district -> new RegionTreeVo(district.getId(), district.getName(), null))
                                .toList();
                        return new RegionTreeVo(city.getId().longValue(), city.getName(), districtNodes);
                    })
                    .toList();

            provinceNode.setChildren(cityNodes);
            return provinceNode;
        }).toList();

        return Result.ok(tree);
    }
}
