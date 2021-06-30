package com.zxgangandy.account.biz.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TransferReqBO {
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 资金转出用户号
     */
    private Long fromUserId;

    /**
     * 资金转入用户号
     */
    private Long toUserId;

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
