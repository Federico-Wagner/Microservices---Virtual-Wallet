package com.virtualWallet.authenticator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String errorMsg;
    private T data;
}
