package com.vladislav.todosclient.services;

import com.proto.todo.*;
import com.vladislav.todosclient.annotations.TryRefreshSession;
import com.vladislav.todosclient.pojo.TaskPojo;
import com.vladislav.todosclient.utils.Utils;
import com.vladislav.todosclient.utils.mappers.TaskMapper;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class TaskService {

    private TaskServiceGrpc.TaskServiceBlockingStub taskBlockingStub;

    private final ObjectFactory<TaskServiceGrpc.TaskServiceBlockingStub> taskServiceBlockingStubFactory;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(
            ObjectFactory<TaskServiceGrpc.TaskServiceBlockingStub> taskServiceBlockingStubFactory,
            TaskMapper taskMapper
    ) {
        this.taskServiceBlockingStubFactory = taskServiceBlockingStubFactory;
        this.taskMapper = taskMapper;
        updateStub();
    }

    @TryRefreshSession
    public List<TaskPojo> getAllTasks(GetAllUserTasksRequest request) {
        final Iterator<GetAllUserTasksResponse> allUserTasks = taskBlockingStub.getAllUserTasks(request);

        return Utils.stream(allUserTasks)
                .map(GetAllUserTasksResponse::getTask)
                .map(taskMapper::toDocument)
                .collect(Collectors.toUnmodifiableList());
    }

    @TryRefreshSession
    public TaskPojo createTask(CreateTaskRequest request) {
        final CreateTaskResponse response = taskBlockingStub.createTask(request);
        final Task task = response.getTask();
        return taskMapper.toDocument(task);
    }

    @TryRefreshSession
    public void updateTask(UpdateTaskRequest request) {
        taskBlockingStub.updateTask(request);
    }

    @TryRefreshSession
    public void deleteTask(DeleteTaskRequest request) {
        taskBlockingStub.deleteTask(request);
    }

    public void updateStub() {
        taskBlockingStub = taskServiceBlockingStubFactory.getObject();
    }
}
