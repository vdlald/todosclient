package com.vladislav.todosclient.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

public class LoginForm extends FormLayout {

    private final TextField usernameField = new TextField("Username") {{
        addClassName("username-field");
        setWidthFull();
        setRequired(true);
        addChangeListener(event -> {
            final TextField field = event.getSource();
            final String value = field.getValue();
            field.setInvalid(value.isEmpty() || value.isBlank());
        });
    }};

    private final PasswordField passwordField = new PasswordField("Password") {{
        addClassName("password-field");
        setWidthFull();
        setRequired(true);
        addChangeListener(event -> {
            final PasswordField field = event.getSource();
            final String value = field.getValue();
            field.setInvalid(value.isEmpty() || value.isBlank());
        });
    }};

    private final Button login = new Button("Log in") {{
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        setWidthFull();

        addClickShortcut(Key.ENTER);

        addClickListener(click -> {
            if (validate()) {
                final String username = usernameField.getValue();
                final String password = passwordField.getValue();
                LoginForm.this.fireEvent(new LoginEvent(LoginForm.this, username, password));
            }
        });
    }};

    private final Div errorMessage = new Div() {{
        addClassName("error-message");
        setVisible(false);
        add(new H5("Incorrect username or password!"),
                new Paragraph("Check that you have entered the correct username and password and try again."));
    }};

    public LoginForm() {
        addClassName("login-form");

        final VerticalLayout layout = new VerticalLayout() {{
            addClassName("layout");
            add(new H2("Log in"), errorMessage, usernameField, passwordField, login);
            setSizeFull();
        }};

        add(layout);
    }

    public void setError(boolean isError) {
        errorMessage.setVisible(isError);
    }

    private boolean validate() {
        usernameField.setInvalid(usernameField.getValue().isEmpty());
        passwordField.setInvalid(passwordField.getValue().isEmpty());
        return !usernameField.isInvalid() && !passwordField.isInvalid();
    }

    public static abstract class LoginFormEvent extends ComponentEvent<LoginForm> {

        @Getter
        private final String username;

        @Getter
        private final String password;

        public LoginFormEvent(LoginForm source, String username, String password) {
            super(source, false);
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginEvent extends LoginFormEvent {
        public LoginEvent(LoginForm source, String username, String password) {
            super(source, username, password);
        }
    }

    public Registration addLoginListener(
            ComponentEventListener<LoginEvent> listener
    ) {
        return getEventBus().addListener(LoginEvent.class, listener);
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener
    ) {
        return getEventBus().addListener(eventType, listener);
    }
}
