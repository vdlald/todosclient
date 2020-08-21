package com.vladislav.todosclient.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectPojo {

    private UUID id;
    private UUID userId;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    private Boolean isDeleted = false;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

}
