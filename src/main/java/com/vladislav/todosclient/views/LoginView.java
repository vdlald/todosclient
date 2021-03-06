package com.vladislav.todosclient.views;

import com.proto.auth.UserAuthenticationRequest;
import com.proto.auth.UserAuthenticationResponse;
import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vladislav.todosclient.ui.LoginForm;
import com.vladislav.todosclient.utils.JwtUtils;
import com.vladislav.todosclient.utils.Utils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@Route("login")
@PageTitle("Login | TODO")
@CssImport("./styles/shared-styles.css")
public class LoginView extends VerticalLayout {

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @SuppressWarnings("all")
    private final JwtUtils jwtUtils;

    private final LoginForm login = new LoginForm();

    public LoginView(UserServiceGrpc.UserServiceBlockingStub userBlockingStub, JwtUtils jwtUtils) {
        this.userBlockingStub = userBlockingStub;
        this.jwtUtils = jwtUtils;

        if (jwtUtils.getCurrentUserId().isPresent()) {
            navigateToMainPage();
            return;
        }

        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.addLoginListener(event -> {
            final String username = event.getUsername();
            final String password = event.getPassword();
            try {
                authUser(username, password);
                navigateToMainPage();
            } catch (StatusRuntimeException e) {
                final Status status = e.getStatus();
                if (status.equals(Status.UNAUTHENTICATED) || status.equals(Status.NOT_FOUND)) {
                    login.setError(true);
                } else {
                    e.printStackTrace();
                }
            }
        });

        final RouterLink registration = new RouterLink("Registration", RegistrationView.class);
        registration.addClassName("registration-link");
        add(
                new H1("TODO"),
                login,
                registration
        );
    }

    private void authUser(String username, String password) {
        final UserAuthenticationRequest request = UserAuthenticationRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        final UserAuthenticationResponse response = userBlockingStub.authenticateUser(request);

        final String jwt = response.getJwt();
        final String refreshToken = response.getRefreshToken();

        final VaadinSession vaadinSession = VaadinSession.getCurrent();
        vaadinSession.setAttribute("jwt", jwt);
        vaadinSession.setAttribute("refresh-token", refreshToken);
    }

    private void navigateToMainPage() {
        Utils.navigateTo(TasksView.class);
    }
}
