package com.vladislav.todosclient.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vladislav.todosclient.pojo.TaskPojo;
import com.vladislav.todosclient.ui.TaskForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Route("")
@CssImport("./styles/shared-styles.css")
public class TasksView extends VerticalLayout {

    private final Grid<TaskPojo> grid = new Grid<>(TaskPojo.class);
    private final TextField filterText = new TextField();
    private final TaskForm taskForm = new TaskForm();

    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public TasksView(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        addClassName("task-list");
        setSizeFull();

        configureGrid();
        configureFilter();

        final Div content = new Div(grid, taskForm);
        content.addClassName("content");
        content.setSizeFull();

        add(filterText, content);
        updateGrid();
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filter by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateGrid());
    }

    private void updateGrid() {
        final List<TaskPojo> tasks = List.of(
                new TaskPojo()
                        .setId(UUID.randomUUID())
                        .setUserId(UUID.randomUUID())
                        .setProjectId(UUID.randomUUID())
                        .setTitle("First task")
                        .setContent("First body")
                        .setCompleted(false)
                        .setDeadline(LocalDateTime.now().plusDays(3))
                        .setCreatedAt(LocalDateTime.now())
                        .setIsDeleted(false),
                new TaskPojo()
                        .setId(UUID.randomUUID())
                        .setUserId(UUID.randomUUID())
                        .setProjectId(UUID.randomUUID())
                        .setTitle("Second task")
                        .setContent("Second body")
                        .setCompleted(true)
                        .setDeadline(LocalDateTime.now())
                        .setCreatedAt(LocalDateTime.now())
                        .setCompletedAt(LocalDateTime.now())
                        .setIsDeleted(false));
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
            checkbox.setEnabled(false);
            return checkbox;
        }).setHeader("Completed");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));
    }
}
