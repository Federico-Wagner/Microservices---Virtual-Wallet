package com.virtualWallet.AuthenticatorMs.service;

import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import com.virtualWallet.AuthenticatorMs.enumerators.eService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;


class PermissionServiceTest {

    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionService();
    }

    @Test
    void getAllowedRoles_shouldReturnCorrectRolesForLogin() {
        Set<RolesEnum> roles = permissionService.getAllowedRoles(eService.LOGIN);
        assertTrue(roles.contains(RolesEnum.CLIENT));
        assertEquals(2, roles.size());
    }

    @Test
    void checkRoleAccessForService_shouldReturnTrueForAllowedRole() {
        assertTrue(permissionService.checkRoleAccessForService(eService.LOGIN, RolesEnum.CLIENT));
        assertFalse(permissionService.checkRoleAccessForService(eService.TRANSACTION, RolesEnum.SUPPORT));
    }

}
