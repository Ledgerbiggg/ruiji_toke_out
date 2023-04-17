package com.ledger.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Orders;
import com.ledger.reggie.dto.OrdersDto;
import com.ledger.reggie.service.OrderService;
import javafx.scene.input.DataFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ledger
 * @version 1.0
 **/
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        return orderService.submit(orders);
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getOrderList(Long page, Long pageSize){
        return orderService.getOrderList(page,pageSize);
    }

    @GetMapping("/page")
    public R<Page<OrdersDto>> getOrderPage(Long page, Long pageSize, Long number, String beginTime,String endTime){
        if(beginTime!=null&&endTime!=null){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime beginTime1 = LocalDateTime.parse(beginTime, dateTimeFormatter);
            LocalDateTime endTime1 = LocalDateTime.parse(endTime, dateTimeFormatter);
            return orderService.getOrderPage(page,pageSize,number,beginTime1,endTime1);
        }
        return orderService.getOrderPage(page,pageSize,number,null,null);
    }



}
