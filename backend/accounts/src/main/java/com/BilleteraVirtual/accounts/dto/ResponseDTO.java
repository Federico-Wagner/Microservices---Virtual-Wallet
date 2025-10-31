package com.BilleteraVirtual.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO<T> {
    private boolean success;
    private String errorMsg;
    private T data;


    public ResponseDTO(boolean success, T data){
        this.success = success;
        this.data = data;
    }

    public ResponseDTO(boolean success, String errorMsg){
        this.success = success;
        this.errorMsg = errorMsg;
    }

}
