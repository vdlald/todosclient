package com.vladislav.todosclient.views;

import com.proto.auth.AuthenticateUserRequest;
import com.proto.auth.AuthenticateUserResponse;
import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.component.UI;
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
                final String jwt = authUser(username, password);
                VaadinSession.getCurrent().setAttribute("jwt", jwt);
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

    private String authUser(String username, String password) {
        final AuthenticateUserRequest request = AuthenticateUserRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        final AuthenticateUserResponse response = userBlockingStub.authenticateUser(request);
        return response.getJwt();
    }

    private void navigateToMainPage() {
        Utils.navigateTo(TasksView.class);
    }
}
