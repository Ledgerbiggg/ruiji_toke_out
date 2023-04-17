package com.ledger.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ledger.reggie.domian.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ledger
 * @version 1.0
 **/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
