package com.imooc.controller;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "地址相关的接口",tags = "地址相关的接口")
@RequestMapping("/address")
@RestController
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("add")
    @ApiOperation(value = "添加地址",tags = "添加地址")
    public IMOOCJSONResult add(@RequestBody AddressBO addressBO){
        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.add(addressBO);
        return IMOOCJSONResult.ok();
    }

    @PostMapping("update")
    @ApiOperation(value = "修改地址",tags = "修改地址")
    public IMOOCJSONResult update(@RequestBody AddressBO addressBO){
        if (StringUtils.isBlank(addressBO.getAddressId())){
            return IMOOCJSONResult.errorMsg("地址id不能为空");
        }

        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }
        addressService.update(addressBO);
        return IMOOCJSONResult.ok();
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除地址",tags = "删除地址")
    public IMOOCJSONResult delete(@RequestParam String addressId){
        if (StringUtils.isBlank(addressId)){
            return IMOOCJSONResult.errorMsg("addressId不能为空");
        }
        String str = addressService.delete(addressId);
        return IMOOCJSONResult.ok(str);
    }

    @PostMapping("setDefalut")
    @ApiOperation(value = "设置默认地址",tags = "设置默认地址")
    public IMOOCJSONResult setDefalut(@RequestParam String addressId,
                                      @RequestParam String userId){
        if (StringUtils.isBlank(addressId)||StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("addressId不能为空");
        }
        addressService.setDefalut(addressId,userId);
        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return IMOOCJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return IMOOCJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
        }

        return IMOOCJSONResult.ok();
    }

    @PostMapping("list")
    @ApiOperation(value = "根据userId查询地址",tags = "根据userId查询地址")
    public IMOOCJSONResult list(@RequestParam String userId){
        if (StringUtils.isBlank(userId)){
            IMOOCJSONResult.errorMsg("用户id不能为空");
        }

        List<UserAddress> userAddresses = addressService.queryAddressByUserId(userId);
        return IMOOCJSONResult.ok(userAddresses);
    }


}
