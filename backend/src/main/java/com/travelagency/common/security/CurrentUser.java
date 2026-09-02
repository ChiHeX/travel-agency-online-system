package com.travelagency.common.security;

import com.travelagency.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static UserPrincipal required() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(401, "请先登录");
        }
        return principal;
    }

    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (String role : roles) {
            if (authentication.getAuthorities().stream().anyMatch(a ->
                    a.getAuthority().equals(role) || a.getAuthority().equals("ROLE_" + role))) {
                return true;
            }
        }
        return false;
    }
}
