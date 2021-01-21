package com.zxgangandy.account.rest.controller;

import com.zxgangandy.account.biz.service.ISpotAccountService;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import com.zxgangandy.account.model.dto.FrozenAccountRespDTO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import com.zxgangandy.account.model.dto.UnfrozenRespDTO;
import com.zxgangandy.account.rest.converter.FrozenReqConverter;
import com.zxgangandy.account.rest.converter.UnfrozenReqConverter;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static io.jingwei.base.utils.constant.ApiConstant.V_1;


@RestController
@RequestMapping("/")
@AllArgsConstructor
public class AccountController {
    private final ISpotAccountService   spotAccountService;
    private final FrozenReqConverter    frozenReqConverter;
    private final UnfrozenReqConverter  unfrozenReqConverter;

    @PostMapping(V_1 + "/account/frozen")
    public ResponseEntity<FrozenAccountRespDTO> frozenAccount(@RequestBody @Valid FrozenAccountReqDTO req) {
        spotAccountService.frozen(frozenReqConverter.to(req));

        return ResponseEntity.ok().body(new FrozenAccountRespDTO().setOrderId(req.getOrderId()));
    }

    @PostMapping(V_1 + "/account/unfrozen")
    public ResponseEntity<UnfrozenRespDTO> unfrozenAccount(@RequestBody @Valid UnfrozenReqDTO req) {
        spotAccountService.unfrozen(unfrozenReqConverter.to(req));

        return ResponseEntity.ok().body(new UnfrozenRespDTO().setOrderId(req.getOrderId()));
    }
}
