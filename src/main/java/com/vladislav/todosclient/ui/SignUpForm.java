package com.vladislav.todosclient.ui;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

public class SignUpForm extends FormLayout {

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

    private final PasswordField passwordRepeatField = new PasswordField("Password repeat") {{
        addClassName("password-field");
        setWidthFull();
        setRequired(true);
        addChangeListener(event -> {
            final PasswordField field = event.getSource();
            final String value = field.getValue();
            field.setInvalid(value.isEmpty() || value.isBlank());
        });
    }};

    private final Button signUp = new Button("Sign up") {{
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        setWidthFull();

        addClickShortcut(Key.ENTER);

        addClickListener(click -> {
            if (validate()) {
                final String username = usernameField.getValue();
                final String password = passwordField.getValue();
                SignUpForm.this.fireEvent(new SignUpForm.SignUpEvent(SignUpForm.this, username, password));
            }
        });
    }};

    public SignUpForm() {
        addClassName("signup-form");

        final VerticalLayout layout = new VerticalLayout() {{
            addClassName("layout");
            add(new H2("Sign up"), usernameField, passwordField, passwordRepeatField, signUp);
        }};

        add(layout);
    }

    private boolean validate() {
        usernameField.setInvalid(usernameField.getValue().isEmpty());

        final String password = passwordField.getValue();
        final String passwordRepeat = passwordRepeatField.getValue();

        passwordField.setInvalid(password.isEmpty());
        passwordRepeatField.setInvalid(passwordRepeat.isEmpty());

        return !usernameField.isInvalid() && !passwordField.isInvalid()
                && passwordRepeatField.isInvalid() && password.equals(passwordRepeat);
    }

    public static abstract class SignUpFormEvent extends ComponentEvent<SignUpForm> {

        @Getter
        private final String username;

        @Getter
        private final String password;

        public SignUpFormEvent(SignUpForm source, String username, String password) {
            super(source, false);
            this.username = username;
            this.password = password;
        }
    }

    public static class SignUpEvent extends SignUpFormEvent {
        public SignUpEvent(SignUpForm source, String username, String password) {
            super(source, username, password);
        }
    }

    public Registration addSignUpListener(ComponentEventListener<SignUpEvent> listener) {
        return getEventBus().addListener(SignUpEvent.class, listener);
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener
    ) {
        return getEventBus().addListener(eventType, listener);
    }
}
