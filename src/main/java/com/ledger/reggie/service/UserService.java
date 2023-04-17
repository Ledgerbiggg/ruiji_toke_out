package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.User;

import javax.servlet.http.HttpSession;

/**
 * @author ledger
 * @version 1.0
 **/
public interface UserService extends IService<User> {
    R<String> loginOut(HttpSession session);

}
