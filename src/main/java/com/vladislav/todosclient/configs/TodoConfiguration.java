package com.vladislav.todosclient.configs;

import com.proto.auth.UserServiceGrpc;
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
    public UserServiceGrpc.UserServiceBlockingStub todoBlockingStub(ManagedChannel todoChannel) {
        return UserServiceGrpc.newBlockingStub(todoChannel);
    }
}
