package com.imooc.service.impl;

import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void add(AddressBO addressBO) {
        List<UserAddress> userAddresses = this.queryAddressByUserId(addressBO.getUserId());
        Integer isDefault = 0;

        if (userAddresses == null || userAddresses.isEmpty() || userAddresses.size() == 0){
            isDefault = 1;
        }

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        String addressId = sid.nextShort();

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAddressByUserId(String userId) {
        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        return userAddressMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void update(AddressBO addressBO) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        userAddress.setId(addressBO.getAddressId());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKey(userAddress);
    }

    @Override
    public String delete(String addressId) {
        UserAddress userAddress = userAddressMapper.selectByPrimaryKey(addressId);
        if (userAddress.getIsDefault() == 1){
            return "默认地址不能删除";
        }

        userAddressMapper.deleteByPrimaryKey(addressId);
        return "ok";
    }

    @Override
    public void setDefalut(String addressId,String userId) {
        List<UserAddress> userAddresses = this.queryAddressByUserId(userId);
        for (UserAddress userAddress : userAddresses) {
            if (userAddress.getIsDefault() == 1){
                userAddress.setIsDefault(0);
                userAddressMapper.updateByPrimaryKeySelective(userAddress);
            }

        }

        UserAddress userAddress = new UserAddress();
        userAddress.setIsDefault(YesOrNoEnum.YES.type);
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String addressId, String userId) {
        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("id",addressId);
        return userAddressMapper.selectOneByExample(example);
    }
}
