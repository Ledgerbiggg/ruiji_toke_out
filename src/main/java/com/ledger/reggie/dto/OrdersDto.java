package com.ledger.reggie.dto;

import com.ledger.reggie.domian.OrderDetail;
import com.ledger.reggie.domian.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    //收货人
    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
