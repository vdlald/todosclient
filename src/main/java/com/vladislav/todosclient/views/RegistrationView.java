package com.vladislav.todosclient.views;

import com.proto.auth.UserServiceGrpc;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vladislav.todosclient.ui.SignUpForm;
import com.vladislav.todosclient.utils.AuthUtils;

@Route("signup")
@PageTitle("Sign Up | TODO")
@CssImport("./styles/shared-styles.css")
public class RegistrationView extends VerticalLayout {

    private final UserServiceGrpc.UserServiceBlockingStub userBlockingStub;
    private final AuthUtils authUtils;

    private final SignUpForm signUpForm = new SignUpForm();

    public RegistrationView(UserServiceGrpc.UserServiceBlockingStub userBlockingStub, AuthUtils authUtils) {
        this.userBlockingStub = userBlockingStub;
        this.authUtils = authUtils;

        if (authUtils.checkAuth()) {
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
    }

    private void navigateToMainPage() {
        UI.getCurrent().navigate(TasksView.class);
        UI.getCurrent().getPage().reload();
    }
}
