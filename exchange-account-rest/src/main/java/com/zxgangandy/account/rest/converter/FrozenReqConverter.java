package com.zxgangandy.account.rest.converter;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import io.jingwei.base.utils.model.BasicObjectMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FrozenReqConverter extends BasicObjectMapper<FrozenAccountReqDTO, FrozenReqBO> {

}
