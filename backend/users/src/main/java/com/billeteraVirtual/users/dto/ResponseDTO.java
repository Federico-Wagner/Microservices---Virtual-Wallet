package com.billeteraVirtual.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String ErrorMsg;
    private T data;
}
