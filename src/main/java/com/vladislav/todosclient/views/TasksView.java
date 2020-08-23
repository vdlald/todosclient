package com.vladislav.todosclient.views;

import com.proto.todo.GetAllUserTasksRequest;
import com.proto.todo.GetAllUserTasksResponse;
import com.proto.todo.ProjectServiceGrpc;
import com.proto.todo.TaskServiceGrpc;
import com.vaadin.flow.component.UI;
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
import com.vladislav.todosclient.ui.TaskForm;
import com.vladislav.todosclient.utils.JwtUtils;
import com.vladislav.todosclient.utils.Utils;
import com.vladislav.todosclient.utils.mappers.TaskMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PageTitle("All tasks | TODO")
@Route(value = "", layout = MainLayout.class)
public class TasksView extends VerticalLayout {

    private final Grid<TaskPojo> grid = new Grid<>(TaskPojo.class);
    private final TextField filterText = new TextField();
    private final TaskForm taskForm = new TaskForm();

    private final TaskMapper taskMapper;
    private final DateTimeFormatter dateTimeFormatter;
    private final JwtUtils jwtUtils;
    private final TaskServiceGrpc.TaskServiceBlockingStub taskBlockingStub;
    private final ProjectServiceGrpc.ProjectServiceBlockingStub projectBlockingStub;

    public TasksView(
            TaskMapper taskMapper,
            DateTimeFormatter dateTimeFormatter,
            JwtUtils jwtUtils,
            TaskServiceGrpc.TaskServiceBlockingStub taskBlockingStub,
            ProjectServiceGrpc.ProjectServiceBlockingStub projectBlockingStub
    ) {
        this.taskMapper = taskMapper;
        this.dateTimeFormatter = dateTimeFormatter;
        this.jwtUtils = jwtUtils;
        this.taskBlockingStub = taskBlockingStub;
        this.projectBlockingStub = projectBlockingStub;

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

        final List<TaskPojo> tasks = Utils.stream(taskBlockingStub.getAllUserTasks(request))
                .map(GetAllUserTasksResponse::getTask)
                .map(taskMapper::toDocument)
                .collect(Collectors.toUnmodifiableList());

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
        // todo: implement
    }

    private void deleteTask(TaskPojo task) {
        // todo: implement
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
        UI.getCurrent().navigate(LoginView.class);
        UI.getCurrent().getPage().reload();
    }
}
