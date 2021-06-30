package com.zxgangandy.account.biz.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UnfrozenWithdrawReqBO {
    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 解冻业务ID(如果是一次解冻可以和订单号一样)
     */
    private Long bizId;

    /**
     * 用户号
     */
    private Long userId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 解冻金额，入金金额
     */
    private BigDecimal amount;

}
