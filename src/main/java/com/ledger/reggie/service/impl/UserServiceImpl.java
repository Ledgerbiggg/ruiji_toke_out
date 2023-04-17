package com.ledger.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.common.BaseContext;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.User;
import com.ledger.reggie.mapper.UserMapper;
import com.ledger.reggie.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public R<String> loginOut(HttpSession session) {
        session.removeAttribute("user");
        return R.success("退出登录侧国行");
    }
}
