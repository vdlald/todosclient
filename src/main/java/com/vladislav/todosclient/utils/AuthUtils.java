package com.vladislav.todosclient.utils;

import com.vaadin.flow.server.VaadinRequest;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final JwtParser jwtParser;

    public boolean checkAuth() {
        final VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
        final Cookie[] cookies = vaadinRequest.getCookies();
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                final String jwt = cookie.getValue();
                try {
                    jwtParser.parseClaimsJws(jwt);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
