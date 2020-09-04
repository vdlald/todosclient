package com.vladislav.todosclient.configs;

import com.proto.todo.ProjectServiceGrpc;
import com.proto.todo.TaskServiceGrpc;
import com.vaadin.flow.server.VaadinSession;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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
    @Scope("prototype")
    public TaskServiceGrpc.TaskServiceBlockingStub taskBlockingStub(ManagedChannel todoChannel) {
        return attachJwt(TaskServiceGrpc.newBlockingStub(todoChannel));
    }

    @Bean
    @Scope("prototype")
    public ProjectServiceGrpc.ProjectServiceBlockingStub projectBlockingStub(ManagedChannel todoChannel) {
        return attachJwt(ProjectServiceGrpc.newBlockingStub(todoChannel));
    }

    private <T extends AbstractStub<T>> T attachJwt(T stub) {
        final String jwt = (String) VaadinSession.getCurrent().getAttribute("jwt");
        if (jwt != null) {
            final Metadata.Key<String> jwtKey = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);
            final Metadata metadata = new Metadata();
            metadata.put(jwtKey, jwt);
            return MetadataUtils.attachHeaders(stub, metadata);
        }
        return stub;
    }
}
