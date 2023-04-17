package com.ledger.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ledger.reggie.domian.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ledger
 * @version 1.0
 **/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
