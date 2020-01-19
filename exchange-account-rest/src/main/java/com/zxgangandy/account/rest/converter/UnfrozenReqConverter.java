package com.zxgangandy.account.rest.converter;

import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import com.zxgangandy.base.utils.model.BasicObjectMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnfrozenReqConverter extends BasicObjectMapper<UnfrozenReqDTO, UnfrozenReqBO> {

}
