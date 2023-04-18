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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();
        if (StrUtil.isNotBlank(phone)) {
            //生成随机数四个
            String code = RandomUtil.randomNumbers(4);
            log.info(code);
            //调用发送短信的业务
            SMSUtils.sendMessage("瑞吉外卖", "SMS_276445657", phone, code);
            //同时保存手机号和验证码
            stringRedisTemplate.opsForValue().set("code:" + phone, code, 5, TimeUnit.MINUTES);
        }
        return R.error("短信发送失败");
    }

    @PostMapping("login")
    public R<User> login(@RequestBody Map<String,String> map) {
        //获取发送过来的手机号和验证码(要做非空判断)
        long phone = Long.parseLong(map.get("phone"));
        long code = Long.parseLong(map.get("code"));
        String check = stringRedisTemplate.opsForValue().get("code:" + phone);
        if (phone == 0 || code == 0) {
            return R.error(null);
        }
        if (Long.toString(code).equals(check)) {
            User user = new User();
            user.setPhone(Long.toString(phone));
            userService.save(user);
            stringRedisTemplate.delete("code:" + phone);
            return R.success(user);
        }
        return R.error(null);
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpSession session) {
        return userService.loginOut(session);
    }
}
