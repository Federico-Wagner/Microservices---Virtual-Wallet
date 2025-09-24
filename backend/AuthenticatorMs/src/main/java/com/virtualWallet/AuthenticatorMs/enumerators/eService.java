package com.virtualWallet.AuthenticatorMs.enumerators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum eService {

    LOGIN("LOGIN"),
    GETACCOUNTS("GETACCOUNTS"),
    TRANSACTION("TRANSACTION"),

    ;

    private final String id;


    public static eService getEnum(String enumStr){
        return eService.valueOf(enumStr);
    }

//    public String getId(){
//        return this.id;
//    }

}
