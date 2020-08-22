package com.vladislav.todosclient.utils;

import com.vaadin.flow.server.VaadinRequest;
import lombok.experimental.UtilityClass;

import javax.servlet.http.Cookie;

@UtilityClass
public class AuthChecker {

    public boolean checkAuth() {
        boolean isAuth = false;
        for (Cookie cookie : VaadinRequest.getCurrent().getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                // todo: check jwt
                isAuth = true;
            }
        }
        return isAuth;
    }
}
