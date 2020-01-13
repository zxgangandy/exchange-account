package com.zxgangandy.account.biz.mapper;

import com.zxgangandy.account.biz.entity.SpotAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
public interface SpotAccountMapper extends BaseMapper<SpotAccount> {

    /**
     * 根据用户uid和currency冻结用户账户
     */
    int frozenByUser(@Param("userId") long userId,
                     @Param("currency") String currency,
                     @Param("amount") BigDecimal amount);

    /**
     * 根据用户账户id冻结用户账户
     */
    int frozenByAccount(@Param("accountId") long accountId,
                        @Param("amount") BigDecimal amount);

    /**
     * 根据用户uid和currency解冻用户账户
     */
    int unfrozenByUser(@Param("userId") long userId,
                       @Param("currency") String currency,
                       @Param("amount") BigDecimal amount);

    /**
     * 根据用户账户id解冻用户账户
     */
    int unfrozenByAccount(@Param("accountId") long accountId,
                        @Param("amount") BigDecimal amount);

}
