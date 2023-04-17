package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.AddressBook;

/**
 * @author ledger
 * @version 1.0
 **/

public interface AddressBookService extends IService<AddressBook> {
    R<String> setDefault(AddressBook addressBook);

    R<AddressBook> getDefault();
}
