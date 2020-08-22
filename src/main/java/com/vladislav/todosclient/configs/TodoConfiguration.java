package com.vladislav.todosclient.configs;

import com.proto.todo.ProjectServiceGrpc;
import com.proto.todo.TaskServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TodoConfiguration {

    @Bean
    public ManagedChannel todoChannel(
            @Value("${app.grpc.todo-service.host}") String host,
            @Value("${app.grpc.todo-service.port}") Integer port
    ) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public TaskServiceGrpc.TaskServiceBlockingStub taskBlockingStub(ManagedChannel todoChannel) {
        return TaskServiceGrpc.newBlockingStub(todoChannel);
    }

    @Bean
    public ProjectServiceGrpc.ProjectServiceBlockingStub projectBlockingStub(ManagedChannel todoChannel) {
        return ProjectServiceGrpc.newBlockingStub(todoChannel);
    }
}
