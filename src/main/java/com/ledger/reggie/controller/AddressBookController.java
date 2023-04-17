package com.ledger.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.reggie.common.BaseContext;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.AddressBook;
import com.ledger.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/

@Slf4j
@RestController
@RequestMapping("addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> getAddressList(){
        Long userId = BaseContext.getCurrent();
        LambdaQueryWrapper<AddressBook> lqw=new LambdaQueryWrapper<>();
        lqw.eq(userId!=null,AddressBook::getUserId,userId);
        List<AddressBook> list = addressBookService.list(lqw);
        return R.success(list);
    }

    @PostMapping
    public R<String> saveAddress(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrent());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }


    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        return addressBookService.setDefault(addressBook);
    }
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        return addressBookService.getDefault();
    }






















}
