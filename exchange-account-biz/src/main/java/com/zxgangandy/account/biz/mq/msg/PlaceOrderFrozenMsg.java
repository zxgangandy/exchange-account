package com.zxgangandy.account.biz.mq.msg;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class PlaceOrderFrozenMsg {
    /**
     * 订单号
     */
    private long orderId;
    /**
     * 用户号
     */
    private long userId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 冻结数量（金额）
     */
    private BigDecimal amount;

}
