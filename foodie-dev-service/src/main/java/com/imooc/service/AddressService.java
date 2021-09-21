package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    public void add(AddressBO addressBO);

    public List<UserAddress> queryAddressByUserId(String userId);

    public void update(AddressBO addressBO);

    public String delete(String addressId);

    public void setDefalut(String addressId,String userId);

    /**
     *
     * @param addressId
     * @param userId
     * @return
     */
    public UserAddress queryUserAddress(String addressId,String userId);
}
