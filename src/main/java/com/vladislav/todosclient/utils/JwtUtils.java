package com.vladislav.todosclient.utils;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtParser jwtParser;

    public Optional<String> getCurrentUserId() {
        final VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
        final Cookie[] cookies = vaadinRequest.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                final String jwt = cookie.getValue();
                try {
                    final Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
                    final String userId = (String) jws.getBody().get("userId");
                    return Optional.of(userId);
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }
}
