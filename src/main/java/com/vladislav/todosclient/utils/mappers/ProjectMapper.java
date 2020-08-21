package com.vladislav.todosclient.utils.mappers;

import com.proto.todo.Project;
import com.vladislav.todosclient.pojo.ProjectPojo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class ProjectMapper implements PojoMapper<ProjectPojo, Project> {

    @Override
    public ProjectPojo toDocument(Project project) {
        return new ProjectPojo()
                .setId(UUID.fromString(project.getId()))
                .setUserId(UUID.fromString(project.getUserId()))
                .setName(project.getName())
                .setIsDeleted(project.getIsDeleted())
                .setCreatedAt(Instant.ofEpochMilli(project.getCreatedAt()).atOffset(ZoneOffset.UTC).toLocalDateTime());
    }

    @Override
    public Project toDto(ProjectPojo document) {
        return Project.newBuilder()
                .setId(document.getId().toString())
                .setUserId(document.getUserId().toString())
                .setName(document.getName())
                .setIsDeleted(document.getIsDeleted())
                .setCreatedAt(document.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .build();
    }
}
