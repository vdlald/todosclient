package com.vladislav.todosclient.views;

import com.proto.auth.AuthenticateUserRequest;
import com.proto.auth.AuthenticateUserResponse;
import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.grpc.StatusRuntimeException;

@Route("login")
@PageTitle("Login | TODO")
public class LoginView extends VerticalLayout {

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    private final LoginForm login = new LoginForm();

    public LoginView(UserServiceGrpc.UserServiceBlockingStub userBlockingStub) {
        this.userBlockingStub = userBlockingStub;
        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");
        login.addLoginListener(event -> {
            final String username = event.getUsername();
            final String password = event.getPassword();
            try {
                final AuthenticateUserRequest request = AuthenticateUserRequest.newBuilder()
                        .setUsername(username)
                        .setPassword(password)
                        .build();
                final AuthenticateUserResponse response = userBlockingStub.authenticateUser(request);
                final String jwt = response.getJwt();
                VaadinSession.getCurrent().setAttribute("jwt", jwt);
                UI.getCurrent().navigate("");
            } catch (StatusRuntimeException e) {
                login.setError(true);
            }
        });

        add(
                new H1("TODO"),
                login
        );
    }
}
