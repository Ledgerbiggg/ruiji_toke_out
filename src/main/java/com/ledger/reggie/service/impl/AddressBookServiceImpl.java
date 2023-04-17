package com.ledger.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.BaseContext;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.AddressBook;
import com.ledger.reggie.mapper.AddressBookMapper;
import com.ledger.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ledger
 * @version 1.0
 **/
@Transactional
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public R<String> setDefault(AddressBook addressBook) {
        //设置这个用户的默认地址
        Long userId = BaseContext.getCurrent();
        LambdaUpdateWrapper<AddressBook> luw=new LambdaUpdateWrapper<>();
        luw.eq(AddressBook::getUserId,userId);
        luw.set(AddressBook::getIsDefault,0);
        update(luw);
        addressBook.setIsDefault(1);
        addressBook.setUserId(userId);
        updateById(addressBook);
        return R.success("更新默认地址成功");
    }

    @Override
    public R<AddressBook> getDefault() {
        //获取这个用户的默认地址
        Long userId = BaseContext.getCurrent();
        LambdaQueryWrapper<AddressBook> lqw=new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getIsDefault,1);
        lqw.eq(AddressBook::getUserId,userId);
        return R.success(getOne(lqw));
    }
}
