package com.vladislav.todosclient.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskPojo {

    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String title;
    private String content;
    private Boolean completed;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Boolean isDeleted;

}
