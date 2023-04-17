package com.ledger.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.reggie.domian.OrderDetail;
import com.ledger.reggie.mapper.OrderDetailMapper;
import com.ledger.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author ledger
 * @version 1.0
 **/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper,OrderDetail> implements OrderDetailService {

}
