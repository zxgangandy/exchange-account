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
 * 现货账户流水日志
 * </p>
 *
 * @author Andy
 * @since 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SpotAccountLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资金转出用户号
     */
    private Long fromUserId;

    /**
     * 资金转入用户号
     */
    private Long toUserId;

    /**
     * 资金转出账务号
     */
    private Long fromAccountId;

    /**
     * 资金转入账户号
     */
    private Long toAccountId;

    /**
     * 账号当前余额
     */
    private BigDecimal balance;

    /**
     * 账号当前冻结量
     */
    private BigDecimal frozen;

    /**
     * 业务订单号
     */
    private Long orderId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务操作数量/金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;


}
