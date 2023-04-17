package com.ledger.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Orders;
import com.ledger.reggie.dto.OrdersDto;

import java.time.LocalDateTime;

/**
 * @author ledger
 * @version 1.0
 **/
public interface OrderService extends IService<Orders> {
    R<String> submit(Orders orders);

    R<Page<OrdersDto>> getOrderList(Long page, Long pageSize);

    R<Page<OrdersDto>> getOrderPage(Long page, Long pageSize, Long number,LocalDateTime beginTime, LocalDateTime endTime);
}
