package com.atguigu.lease.web.app.controller.apartment;

import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "公寓信息")
@RequestMapping("/app/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Operation(summary = "根据id获取公寓信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo detailVo = apartmentInfoService.getDetailById(id);
        return Result.ok(detailVo);
    }

    @Operation(summary = "根据区县id获取公寓列表")
    @GetMapping("listByDistrictId")
    public Result<List<ApartmentItemVo>> listByDistrictId(@RequestParam Long districtId) {
        List<ApartmentItemVo> list = apartmentInfoService.listByDistrictId(districtId);
        return Result.ok(list);
    }

    @Operation(summary = "根据条件查询公寓列表")
    @GetMapping("listByQuery")
    public Result<List<ApartmentItemVo>> listByQuery(
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long districtId) {
        List<ApartmentItemVo> list = apartmentInfoService.listByQuery(provinceId, cityId, districtId);
        return Result.ok(list);
    }
}
