package com.billeteraVirtual.transacciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String errorMsg;
    private T data;


    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, null, data);
    }

    public static <T> ResponseDTO<T> failure(String message) {
        return new ResponseDTO<>(false, message, null);
    }

}
