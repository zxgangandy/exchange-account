package com.zxgangandy.account.biz.mapper;

import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 现货账户 Mapper 接口
 * </p>
 *
 * @author Andy
 * @since 2021-01-22
 */
public interface SpotAccountFrozenMapper extends BaseMapper<SpotAccountFrozen> {
    /**
     * 根据用户uid、orderId、bizType更新订单冻结金额
     */
    int updateOrderFrozen(@Param("uid") long uid,
                          @Param("orderId") long orderId,
                          @Param("bizType") String bizType,
                          @Param("amount") BigDecimal amount);
}
