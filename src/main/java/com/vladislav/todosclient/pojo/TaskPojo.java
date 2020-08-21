package com.vladislav.todosclient.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "set")
public class TaskPojo {

    private UUID id;
    private UUID userId;
    private UUID projectId;

    @NotNull
    @NotEmpty
    private String title;

    @NotNull
    @NotEmpty
    private String content;

    @NotNull
    private Boolean completed = false;

    private LocalDateTime deadline;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    @NotNull
    private Boolean isDeleted = false;

}
