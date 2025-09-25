package com.billeteraVirtual.users.enumerators;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RolesEnum {

    ADMIN("ADMIN"),
    CLIENT("CLIENT"),
    SUPPORT("SUPPORT");

    public final String id;

    public static RolesEnum getEnum(String enumStr){
        return RolesEnum.valueOf(enumStr);
    }

}
