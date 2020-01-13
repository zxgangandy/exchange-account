package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class FrozenAccountRespDTO {
    /**
     * 订单号
     */
    private long orderId;

}
