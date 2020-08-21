package com.vladislav.todosclient.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vladislav.todosclient.pojo.TaskPojo;
import lombok.Getter;

public class TaskForm extends FormLayout {

    private final TextField title = new TextField("Title");
    private final TextArea content = new TextArea("Content");
    private final DateTimePicker deadline = new DateTimePicker("Deadline");
    private final Checkbox completed = new Checkbox("Completed", false);
    private final Checkbox deleted = new Checkbox("Deleted", false);

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button close = new Button("Close");

    private final Binder<TaskPojo> binder = new BeanValidationBinder<>(TaskPojo.class);

    public TaskForm() {
        addClassName("task-form");

        binder.bindInstanceFields(this);

        add(
                title,
                content,
                deadline,
                deadline,
                new HorizontalLayout(completed, deleted),
                createButtonLayout()
        );
    }

    public void setTask(TaskPojo task) {
        binder.setBean(task);
    }

    private Component createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(close, delete, save);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public static abstract class TaskFormEvent extends ComponentEvent<TaskForm> {

        @Getter
        private final TaskPojo task;

        public TaskFormEvent(TaskForm source, TaskPojo task) {
            super(source, false);
            this.task = task;
        }
    }

    public static class SaveEvent extends TaskFormEvent {
        public SaveEvent(TaskForm source, TaskPojo task) {
            super(source, task);
        }
    }

    public static class DeleteEvent extends TaskFormEvent {
        public DeleteEvent(TaskForm source, TaskPojo task) {
            super(source, task);
        }
    }

    public static class CloseEvent extends TaskFormEvent {
        public CloseEvent(TaskForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener
    ) {
        return getEventBus().addListener(eventType, listener);
    }
}
