package com.virtualWallet.AuthenticatorMs.service;

import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import com.virtualWallet.AuthenticatorMs.enumerators.eService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class PermissionService {

    private final Map<eService, Set<RolesEnum>> serviceRoleMap = new EnumMap<>(eService.class);

    public PermissionService() {
        serviceRoleMap.put(eService.LOGIN, EnumSet.of(RolesEnum.CLIENT, RolesEnum.ADMIN));
        serviceRoleMap.put(eService.GETACCOUNTS, EnumSet.of(RolesEnum.ADMIN, RolesEnum.SUPPORT));
        serviceRoleMap.put(eService.TRANSACTION, EnumSet.of(RolesEnum.CLIENT, RolesEnum.ADMIN));
    }

    public Set<RolesEnum> getAllowedRoles(eService service) {
        return serviceRoleMap.get(service);
    }

    public boolean checkRoleAccessForService(eService service, RolesEnum rol) {
        Set<RolesEnum> rolesEnumEnumSet = getAllowedRoles(service);
        return rolesEnumEnumSet.contains(rol);
    }

}

