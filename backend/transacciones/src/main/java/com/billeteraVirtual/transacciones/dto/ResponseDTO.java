package com.billeteraVirtual.transacciones.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    public boolean success;
    public String errMsg;
    public T data;
}
