package com.zxgangandy.account.biz.mq.msg;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlaceOrderFrozenResultMsg {
    /**
     * 订单号
     */
    private long orderId;
    /**
     * 用户号
     */
    private long userId;

    /**
     * 业务类型
     */
    private String bizType;

}
