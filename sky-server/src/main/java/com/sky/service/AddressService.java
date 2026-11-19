package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressService {
    void addAddress(AddressBook addressBook);

    List<AddressBook> getAddress();

    void updateAddress(AddressBook addressBook);

    void deleteAddressById(Long id);

    AddressBook getAddressById(Long id);

    void updateDefault(AddressBook addressBook);
}
