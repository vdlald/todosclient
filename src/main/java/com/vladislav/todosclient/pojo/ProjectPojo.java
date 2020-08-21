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
public class ProjectPojo {

    private UUID id;
    private UUID userId;
    private String name;
    private Boolean isDeleted;
    private LocalDateTime createdAt;

}
