package com.sky.controller.user;


import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user/addressBook")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public Result addAddress(@RequestBody AddressBook addressBook){

        addressService.addAddress(addressBook);
        return Result.success();
    }

    @GetMapping("list")
    public Result<List<AddressBook>> getAddress(){
        List<AddressBook> addressList = addressService.getAddress();
        return Result.success(addressList);
    }

    @PutMapping
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressService.updateAddress(addressBook);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteAddressById(@RequestParam Long id){
        addressService.deleteAddressById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getAddressById(@PathVariable("id") Long id){
        AddressBook addressBook = addressService.getAddressById(id);
        return Result.success(addressBook);
    }

    /*
    设置默认地址
     */
    @PutMapping("/default")
    public Result updateDefault(@RequestBody AddressBook addressBook){
        addressService.updateDefault(addressBook);
        return Result.success();
    }


}
