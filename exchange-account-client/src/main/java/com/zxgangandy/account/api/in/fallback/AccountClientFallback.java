package com.zxgangandy.account.api.in.fallback;

import com.zxgangandy.account.api.in.AccountClient;
import com.zxgangandy.account.model.dto.FrozenAccountReqDTO;
import com.zxgangandy.account.model.dto.UnfrozenReqDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AccountClientFallback implements AccountClient {

    @Override
    public ResponseEntity frozenAccount(FrozenAccountReqDTO req) {
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity unfrozenAccount(UnfrozenReqDTO req) {
        return ResponseEntity.ok(null);
    }
}
