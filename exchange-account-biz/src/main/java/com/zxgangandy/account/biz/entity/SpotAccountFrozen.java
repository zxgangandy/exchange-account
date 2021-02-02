package com.zxgangandy.account.biz.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 现货账户
 * </p>
 *
 * @author Andy
 * @since 2021-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccountFrozen implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户号
     */
    private Long userId;

    /**
     * 冻结源订单号
     */
    private Long orderId;

    /**
     * 冻结业务类型
     */
    private String bizType;

    /**
     * 币种账号号
     */
    private Long accountId;

    /**
     * 币种
     */
    private String currency;

    /**
     * 订单源冻结持仓量
     */
    private BigDecimal originFrozen;

    /**
     * 剩余冻结量
     */
    private BigDecimal leftFrozen;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
