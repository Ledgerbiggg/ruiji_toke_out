package com.ledger.reggie.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.User;
import com.ledger.reggie.service.UserService;
import com.ledger.reggie.utils.SMSUtils;
import com.sun.org.apache.bcel.internal.classfile.Code;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StrUtil.isNotBlank(phone)) {
            //生成随机数四个
            String code = RandomUtil.randomNumbers(4);
            log.info(code);
            //调用发送短信的业务
            SMSUtils.sendMessage("瑞吉外卖","SMS_276445657",phone,code);
            //同时保存手机号和验证码
            session.setAttribute("phone", phone);
            session.setAttribute("code", code);
        }
        return R.error("短信发送失败");
    }

    @PostMapping("login")
    public R<User> login(@RequestBody Map map, HttpSession session) {

        log.info(map.toString());
        //获取发送过来的手机号和验证码(要做非空判断)
        Long phone = Long.valueOf(map.get("phone").toString());
        Long code = Long.valueOf(map.get("code").toString());
        Long phone1 = Long.valueOf(session.getAttribute("phone").toString());
        Long code1 = Long.valueOf( session.getAttribute("code").toString());
        if(/*phone.longValue()==phone1.longValue()&&code.longValue()==code1.longValue()*/true){
            //登录成功就村用户信息到手机号到数据库
            LambdaQueryWrapper<User> lqw=new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone,phone);
            User user=userService.getOne(lqw);
            if (user==null) {
                user = new User();
                user.setPhone(phone.toString());
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user);
            return R.success(user);
        }
        return R.error(null);
    }
    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session){
        return userService.loginOut(session);
    }
}
