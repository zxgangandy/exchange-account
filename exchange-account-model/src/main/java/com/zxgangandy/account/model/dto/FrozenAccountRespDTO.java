package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FrozenAccountRespDTO {
    /**
     * 订单号
     */
    private long orderId;

    private boolean result;

}
