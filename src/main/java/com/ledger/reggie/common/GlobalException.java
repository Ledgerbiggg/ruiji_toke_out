package com.ledger.reggie.common;

/**
 * @author ledger
 * @version 1.0
 **/

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
//拦截加了RestController注解的controller
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalException {
    //设置一个错误
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //这个一定要写，不然就不知为什么报错
        log.error(ex.getMessage());
        String msg = "";
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            msg = split[2];
            return R.error(msg + "已经存在");
        }
        return R.error("未知错误");
    }

    //设置一个错误
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        //这个一定要写，不然就不知为什么报错
        log.error(ex.getMessage());
        //返回一个消息提示
        return R.error(ex.getMessage());
    }
}
