package com.vladislav.todosclient.utils.mappers;

import com.proto.todo.Task;
import com.vladislav.todosclient.pojo.TaskPojo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class TaskMapper implements PojoMapper<TaskPojo, Task> {
    @Override
    public TaskPojo toDocument(Task task) {
        final TaskPojo.TaskPojoBuilder pojoBuilder = TaskPojo.builder();
        if (!task.getId().isEmpty()) {
            pojoBuilder.setId(UUID.fromString(task.getId()));
        }
        if (!task.getUserId().isEmpty()) {
            pojoBuilder.setUserId(UUID.fromString(task.getUserId()));
        }
        if (!task.getProjectId().isEmpty()) {
            pojoBuilder.setProjectId(UUID.fromString(task.getProjectId()));
        }
        return pojoBuilder
                .setTitle(task.getTitle())
                .setContent(task.getContent())
                .setCompleted(task.getCompleted())
                .setDeadline(Instant.ofEpochMilli(task.getDeadline()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setCreatedAt(Instant.ofEpochMilli(task.getCreatedAt()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setCompletedAt(Instant.ofEpochMilli(task.getCompletedAt()).atOffset(ZoneOffset.UTC).toLocalDateTime())
                .setIsDeleted(task.getIsDeleted())
                .build();
    }

    @Override
    public Task toDto(TaskPojo document) {
        return Task.newBuilder()
                .setId(uuidToString(document.getId()))
                .setUserId(uuidToString(document.getUserId()))
                .setProjectId(uuidToString(document.getProjectId()))
                .setTitle(document.getTitle())
                .setContent(document.getContent())
                .setCompleted(document.getCompleted())
                .setDeadline(localDateTimeToEpochSecond(document.getDeadline()))
                .setCreatedAt(localDateTimeToEpochSecond(document.getCreatedAt()))
                .setCompletedAt(localDateTimeToEpochSecond(document.getCompletedAt()))
                .build();
    }

    private static String uuidToString(UUID uuid) {
        if (uuid == null) {
            return "";
        } else {
            return uuid.toString();
        }
    }

    private static long localDateTimeToEpochSecond(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return 0;
        } else {
            return localDateTime.toEpochSecond(ZoneOffset.UTC);
        }
    }
}
