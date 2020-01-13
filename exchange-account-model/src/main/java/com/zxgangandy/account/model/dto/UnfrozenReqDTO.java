package com.zxgangandy.account.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UnfrozenReqDTO {
    /**
     * 订单号
     */
    private Long orderId;
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
     * 冻结备注
     */
    private String remark;

}
