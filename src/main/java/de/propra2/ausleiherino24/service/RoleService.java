package de.propra2.ausleiherino24.service;

import javax.servlet.http.HttpServletRequest;

public class RoleService {

    private RoleService() {
    }

    public static String getUserRole(HttpServletRequest request) {
        if (request.isUserInRole("user")) {
            return "user";
        } else if (request.isUserInRole("admin")) {
            return "admin";
        } else {
            return "";
        }
    }
}
