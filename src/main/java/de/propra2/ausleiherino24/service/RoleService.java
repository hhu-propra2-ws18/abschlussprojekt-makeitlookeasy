package de.propra2.ausleiherino24.service;

import javax.servlet.http.HttpServletRequest;

public final class RoleService {

    private RoleService() {
    }

    static String getUserRole(final HttpServletRequest request) {
        if (request.isUserInRole("user")) {
            return "user";
        } else if (request.isUserInRole("admin")) {
            return "admin";
        } else {
            return "";
        }
    }
}
