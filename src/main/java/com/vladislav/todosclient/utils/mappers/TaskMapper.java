package com.vladislav.todosclient.utils.mappers;

import com.proto.todo.Task;
import com.vladislav.todosclient.pojo.TaskPojo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class TaskMapper implements PojoMapper<TaskPojo, Task> {
    @Override
    public TaskPojo toDocument(Task task) {
        return new TaskPojo()
                .setId(UUID.fromString(task.getId()))
                .setUserId(UUID.fromString(task.getUserId()))
                .setProjectId(UUID.fromString(task.getProjectId()))
                .setTitle(task.getTitle())
                .setContent(task.getContent())
                .setCompleted(task.getCompleted())
                .setDeadline(Instant.ofEpochMilli(task.getDeadline()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setCreatedAt(Instant.ofEpochMilli(task.getCreatedAt()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setCompletedAt(Instant.ofEpochMilli(task.getCompletedAt()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setIsDeleted(task.getIsDeleted());
    }

    @Override
    public Task toDto(TaskPojo document) {
        return Task.newBuilder()
                .setId(document.getId().toString())
                .setUserId(document.getUserId().toString())
                .setProjectId(document.getProjectId().toString())
                .setTitle(document.getTitle())
                .setContent(document.getContent())
                .setCompleted(document.getCompleted())
                .setDeadline(document.getDeadline().toEpochSecond(ZoneOffset.UTC))
                .setCreatedAt(document.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .setCompletedAt(document.getCompletedAt().toEpochSecond(ZoneOffset.UTC))
                .build();
    }
}
