package com.vladislav.todosclient.aop;

import com.proto.auth.RefreshSessionRequest;
import com.proto.auth.RefreshSessionResponse;
import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.server.VaadinSession;
import com.vladislav.todosclient.exceptions.UnauthenticatedException;
import com.vladislav.todosclient.services.TaskService;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateJwtAspect {

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @Around("@annotation(com.vladislav.todosclient.annotations.TryRefreshSession)")
    public Object tryRefreshSession(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof StatusRuntimeException) {
                StatusRuntimeException e = (StatusRuntimeException) throwable;
                if (e.getStatus().getCode().equals(Status.UNAUTHENTICATED.getCode())) {
                    return tryRefresh(joinPoint);
                } else {
                    throw e;
                }
            } else if (throwable instanceof ExpiredJwtException) {
                return tryRefresh(joinPoint);
            } else {
                throw throwable;
            }
        }
    }

    private Object tryRefresh(ProceedingJoinPoint joinPoint) throws Throwable {
        final VaadinSession vaadinSession = VaadinSession.getCurrent();

        final RefreshSessionRequest refreshSessionRequest = RefreshSessionRequest.newBuilder()
                .setRefreshToken((String) vaadinSession.getAttribute("refresh-token"))
                .build();

        try {
            final RefreshSessionResponse refreshSessionResponse = userBlockingStub
                    .refreshSession(refreshSessionRequest);

            final String refreshToken = refreshSessionResponse.getRefreshToken();
            final String jwt = refreshSessionResponse.getJwt();
            vaadinSession.setAttribute("jwt", jwt);
            vaadinSession.setAttribute("refresh-token", refreshToken);

            final Object aThis = joinPoint.getThis();
            if (aThis instanceof TaskService) {
                TaskService taskService = (TaskService) aThis;
                taskService.updateStub();
            }

            return joinPoint.proceed();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().equals(Status.UNAUTHENTICATED)) {
                throw new UnauthenticatedException();
            } else {
                throw ex;
            }
        }
    }
}
