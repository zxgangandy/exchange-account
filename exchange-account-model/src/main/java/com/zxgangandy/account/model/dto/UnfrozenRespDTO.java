package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UnfrozenRespDTO {
    /**
     * 订单号
     */
    private long orderId;

}
