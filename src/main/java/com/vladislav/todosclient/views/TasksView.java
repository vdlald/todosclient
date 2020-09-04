package com.vladislav.todosclient.views;

import com.proto.todo.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vladislav.todosclient.pojo.TaskPojo;
import com.vladislav.todosclient.services.TaskService;
import com.vladislav.todosclient.ui.TaskForm;
import com.vladislav.todosclient.utils.JwtUtils;
import com.vladislav.todosclient.utils.Utils;
import com.vladislav.todosclient.utils.mappers.TaskMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@PageTitle("All tasks | TODO")
@Route(value = "", layout = MainLayout.class)
public class TasksView extends VerticalLayout {

    private final Grid<TaskPojo> grid = new Grid<>(TaskPojo.class);
    private final TextField filterText = new TextField();
    private final TaskForm taskForm = new TaskForm();

    private final TaskMapper taskMapper;
    private final DateTimeFormatter dateTimeFormatter;
    private final JwtUtils jwtUtils;

    private final TaskService taskService;

    public TasksView(
            TaskMapper taskMapper,
            DateTimeFormatter dateTimeFormatter,
            JwtUtils jwtUtils,
            TaskService taskService
    ) {
        this.taskMapper = taskMapper;
        this.dateTimeFormatter = dateTimeFormatter;
        this.jwtUtils = jwtUtils;
        this.taskService = taskService;

        if (jwtUtils.getCurrentUserId().isEmpty()) {
            navigateToLoginPage();
            return;
        }

        addClassName("task-list");
        setSizeFull();

        configureGrid();

        taskForm.addListener(TaskForm.SaveEvent.class, this::onSaveEvent);
        taskForm.addListener(TaskForm.DeleteEvent.class, this::onDeleteEvent);
        taskForm.addListener(TaskForm.CloseEvent.class, e -> closeEditor());

        final Div content = new Div(grid, taskForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolBar(), content);
        updateGrid();
        closeEditor();
    }

    private void onDeleteEvent(TaskForm.DeleteEvent event) {
        deleteTask(event.getTask());
        updateGrid();
        closeEditor();
    }

    private void onSaveEvent(TaskForm.SaveEvent event) {
        saveTask(event.getTask());
        updateGrid();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterText.setPlaceholder("Filter by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());

        final Button addTask = new Button("Add task", click -> addTask());

        final HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");

        toolbar.add(filterText, addTask);
        return toolbar;
    }

    private void addTask() {
        grid.asSingleSelect().clear();
        editTask(new TaskPojo());
    }

    private void updateGrid() {
        final GetAllUserTasksRequest request = GetAllUserTasksRequest.newBuilder()
                .setUserId(jwtUtils.getCurrentUserId().get())
                .build();

        final List<TaskPojo> tasks = taskService.getAllTasks(request);

        final String filterValue = filterText.getValue();
        if (filterValue == null || filterValue.isBlank() || filterValue.isEmpty()) {
            grid.setItems(tasks);
        } else {
            final Pattern pattern = Pattern.compile(String.format(".*%s.*", filterValue), Pattern.CASE_INSENSITIVE);
            grid.setItems(tasks.stream().filter(task -> task.getTitle().matches(pattern.pattern())));
        }
    }

    private void configureGrid() {
        grid.addClassName("task-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("deadline");
        grid.setColumns("title");

        grid.addColumn(task -> {
            final LocalDateTime deadline = task.getDeadline();
            return deadline.format(dateTimeFormatter);
        }).setHeader("Deadline");

        grid.addComponentColumn(task -> {
            final Checkbox checkbox = new Checkbox(task.getCompleted());
            checkbox.addValueChangeListener(event -> {
                final Boolean completed = event.getValue();
                if (completed) {
                    task.setCompleted(true);
                    task.setCompletedAt(LocalDateTime.now());
                } else {
                    task.setCompleted(false);
                    task.setCompletedAt(null);
                }
                saveTask(task);
            });
            return checkbox;
        }).setHeader("Completed");

        grid.getColumns().forEach(column -> column.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editTask(event.getValue()));
    }

    private void saveTask(TaskPojo task) {
        final UUID taskId = task.getId();
        final Task taskDto = taskMapper.toDto(task);
        if (taskId == null) {
            final CreateTaskRequest request = CreateTaskRequest.newBuilder().setTask(taskDto).build();
            taskService.createTask(request);
        } else {
            final UpdateTaskRequest request = UpdateTaskRequest.newBuilder().setTask(taskDto).build();
            taskService.updateTask(request);
        }
    }

    private void deleteTask(TaskPojo task) {
        final DeleteTaskRequest request = DeleteTaskRequest.newBuilder().setTaskId(task.getId().toString()).build();
        taskService.deleteTask(request);
    }

    private void closeEditor() {
        taskForm.setTask(null);
        taskForm.setVisible(false);
        removeClassName("editing");
        grid.asSingleSelect().clear();
    }

    private void editTask(TaskPojo task) {
        if (task == null) {
            closeEditor();
        } else {
            taskForm.setTask(task);
            taskForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void navigateToLoginPage() {
        Utils.navigateTo(LoginView.class);
    }
}
