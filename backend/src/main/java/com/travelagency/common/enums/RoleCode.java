package com.travelagency.common.enums;

public final class RoleCode {

    public static final String USER = "USER";
    public static final String STAFF = "STAFF";
    public static final String GUIDE = "GUIDE";
    public static final String ADMIN = "ADMIN";

    private RoleCode() {
    }

    public static String authority(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
