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
import com.vaadin.flow.server.VaadinResponse;
import com.vladislav.todosclient.utils.AuthUtils;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

@Route("login")
@PageTitle("Login | TODO")
public class LoginView extends VerticalLayout {

    private static Logger logger = LoggerFactory.getLogger(LoginView.class);

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;
    private final AuthUtils authUtils;

    private final LoginForm login = new LoginForm();

    public LoginView(UserServiceGrpc.UserServiceBlockingStub userBlockingStub, AuthUtils authUtils) {
        this.userBlockingStub = userBlockingStub;
        this.authUtils = authUtils;

        if (authUtils.checkAuth()) {
            navigateToMainPage();
            return;
        }

        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");
        login.addLoginListener(event -> {
            final String username = event.getUsername();
            final String password = event.getPassword();
            try {
                final String jwt = authUser(username, password);
                VaadinResponse.getCurrent().addCookie(new Cookie("jwt", jwt));
                navigateToMainPage();
            } catch (StatusRuntimeException e) {
                e.printStackTrace();
                login.setError(true);
            }
        });

        add(
                new H1("TODO"),
                login
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
        UI.getCurrent().navigate(TasksView.class);
        UI.getCurrent().getPage().reload();
//            UI.getCurrent().getPage().setLocation("");
    }
}
