package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressMapper;
import com.sky.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void addAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressMapper.addAddress(addressBook);
        //不传就用默认值 0"，
        // 但这是有前提的——SQL 里不能出现这一列。你的插入语句是
        //如果想要用默认值sql语句里面不能出现isdefault，要不然就自己手动添加

    }

    @Override
    public List<AddressBook> getAddress() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> addressList = addressMapper.getAddress(userId);
        return addressList;
    }

    @Override
    public void updateAddress(AddressBook addressBook) {
        Long userId=BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressMapper.updateAddress(addressBook);
    }

    @Override
    public void deleteAddressById(Long id) {
        addressMapper.deleteAddressById(id, BaseContext.getCurrentId());
    }

    @Override
    public AddressBook getAddressById(Long id) {
        return addressMapper.getAddressById(id, BaseContext.getCurrentId());
    }

    @Override
    @Transactional
    public void updateDefault(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        //先清空该用户的所有默认标记，再设置当前地址，保证只有一个默认地址
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(addressBook.getId(), userId);
    }


}
