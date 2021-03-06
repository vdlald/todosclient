package com.vladislav.todosclient.views;

import com.proto.auth.UserRegistrationRequest;
import com.proto.auth.UserRegistrationResponse;
import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vladislav.todosclient.ui.SignUpForm;
import com.vladislav.todosclient.utils.JwtUtils;
import com.vladislav.todosclient.utils.Utils;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@Route("signup")
@PageTitle("Sign Up | TODO")
@CssImport("./styles/shared-styles.css")
public class RegistrationView extends VerticalLayout {

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;
    private final JwtUtils jwtUtils;

    private final SignUpForm signUpForm = new SignUpForm();

    public RegistrationView(UserServiceGrpc.UserServiceBlockingStub userBlockingStub, JwtUtils jwtUtils) {
        this.userBlockingStub = userBlockingStub;
        this.jwtUtils = jwtUtils;

        if (jwtUtils.getCurrentUserId().isPresent()) {
            navigateToMainPage();
            return;
        }

        addClassName("signup-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        final Div formWrapper = new Div(signUpForm) {{
            setWidth("331px");
        }};
        add(new H1("TODO"), formWrapper);

        signUpForm.addSignUpListener(event -> {
            final UserRegistrationRequest request = UserRegistrationRequest.newBuilder()
                    .setUsername(event.getUsername())
                    .setPassword(event.getPassword())
                    .build();
            try {
                final UserRegistrationResponse response = userBlockingStub.registerUser(request);
                navigateToLoginView();
            } catch (StatusRuntimeException e) {
                if (e.getStatus().equals(Status.ALREADY_EXISTS)) {
                    signUpForm.usernameIsTaken(true);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void navigateToMainPage() {
        Utils.navigateTo(TasksView.class);
    }

    private void navigateToLoginView() {
        Utils.navigateTo(LoginView.class);
    }
}
