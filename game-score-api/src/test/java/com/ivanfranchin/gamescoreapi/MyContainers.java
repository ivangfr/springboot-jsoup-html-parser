package com.ivanfranchin.gamescoreapi;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public interface MyContainers {

    @Container
    @ServiceConnection
    MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.0");
}
