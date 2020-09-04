package com.vladislav.todosclient.utils;

import com.vaadin.flow.server.VaadinSession;
import com.vladislav.todosclient.annotations.TryRefreshSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtParser jwtParser;

    @TryRefreshSession
    public Optional<String> getCurrentUserId() {
        final String jwt = (String) VaadinSession.getCurrent().getAttribute("jwt");

        try {
            final Jws<Claims> jws = jwtParser.parseClaimsJws(jwt);
            final String userId = (String) jws.getBody().get("userId");
            return Optional.of(userId);
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
