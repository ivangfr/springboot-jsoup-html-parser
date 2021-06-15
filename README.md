# springboot-jsoup-html-parser

The goal of this project is to get a list of games and their scores from a website. The application must parse the website HTML content, get the necessary information, save the data in a database and expose them through a REST API.

> **Note:** In [`kubernetes-minikube-environment`](https://github.com/ivangfr/kubernetes-minikube-environment/tree/master/html-parser-job-cronjob) repository, it's shown how to deploy this project in `Kubernetes` (`Minikube`)

## Project Diagram

![project-diagram](images/project-diagram.png)

## Applications

- ### game-score-api

  [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java Web application that exposes a REST API from where clients can retrieve the game score data stored in `MongoDB` database.

  | Endpoints                 | Description                                                  |
  | ------------------------- | ------------------------------------------------------------ |
  | `GET /api/games [?title]` | returns all game scores or filtered by title with pagination |
  | `GET /api/games/{id}`     | returns a specific game score filtered by id                 |

- ### game-score-collector

  `Spring Boot` Java application responsible for calling the game score website, parse the HTML content (using [`jsoup`](https://jsoup.org/)) and save the data in [`MongoDB`](https://www.mongodb.com/) database. It will be configured to run from time to time in order to keep the application updated about the information the website provides. 

## Prerequisites

- [`Java 11+`](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)

## Start environment

- Open a terminal and navigate to `springboot-jsoup-html-parser` root folder

- Run the command below to start a `MongoDB` Docker container at port `27017`
  ```
  docker-compose up -d
  ```

## Running Applications with Maven

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder

- Execute the following command to run `game-score-collector`
  ```
  ./mvnw clean spring-boot:run --projects game-score-collector \
    -Dspring-boot.run.jvmArguments="-Dspring.data.mongodb.username=gamescoreuser -Dspring.data.mongodb.password=gamescorepass"
  ```
  `game-score-collector` is a Java application that does its job and terminates. Ideally, it will be executed as a cronjob, scheduled to run during specific time intervals.

- Execute the command below to run `game-score-api`
  ```
  ./mvnw clean spring-boot:run --projects game-score-api \
    -Dspring-boot.run.jvmArguments="-Dspring.data.mongodb.username=gamescoreuser -Dspring.data.mongodb.password=gamescorepass"
  ```

## Running Applications as Docker containers

### Build Docker Images

- In a terminal, make sure you are in `springboot-jsoup-html-parser` root folder

- Run the following script to build the Docker images
  - JVM
    ```
    ./docker-build.sh
    ```
  - Native
    ```
    ./docker-build.sh native
    ```
  
### Environment Variables

- **game-score-api**

  | Environment Variable | Description                                                       |
  | -------------------- | ----------------------------------------------------------------- |
  | `MONGODB_HOST`       | Specify host of the `Mongo` database to use (default `localhost`) |
  | `MONGODB_PORT`       | Specify port of the `Mongo` database to use (default `27017`)     |

- **game-score-collector**

  | Environment Variable | Description                                                       |
  | -------------------- | ----------------------------------------------------------------- |
  | `MONGODB_HOST`       | Specify host of the `Mongo` database to use (default `localhost`) |
  | `MONGODB_PORT`       | Specify port of the `Mongo` database to use (default `27017`)     |

### Start Docker Containers

- In a terminal, execute the command below to run `game-score-collector` Docker container
  ```
  docker run --rm --name game-score-collector \
    -e MONGODB_HOST=mongodb \
    -e SPRING_DATA_MONGODB_USERNAME=gamescoreuser \
    -e SPRING_DATA_MONGODB_PASSWORD=gamescorepass \
    --network=springboot-jsoup-html-parser_default \
    ivanfranchin/game-score-collector:1.0.0
  ```

- Then, execute the following command to run `game-score-api` Docker container in detached mode
  ```
  docker run --rm --name game-score-api -p 8080:8080 \
    -e MONGODB_HOST=mongodb \
    -e SPRING_DATA_MONGODB_USERNAME=gamescoreuser \
    -e SPRING_DATA_MONGODB_PASSWORD=gamescorepass \
    --network=springboot-jsoup-html-parser_default \
    ivanfranchin/game-score-api:1.0.0
  ```

## Testing

- In a terminal, the following `curl` command returns the game score results with pagination: page 0, size 10, sorted descending by `score` field.
  ```
  curl -i "http://localhost:8080/api/games?page=0&size=10&sort=score%2Cdesc"
  ```
  You can get a specific game score by running the following `curl` command
  ```
  curl -i http://localhost:8080/api/games/325927
  ```

- You can access `game-score-api` Swagger at http://localhost:8080

## Useful Commands

- **MongoDB**

  List all game scores
  ```
  docker exec -it mongodb mongo -ugamescoreuser -pgamescorepass --authenticationDatabase gamescoredb
  use gamescoredb
  db.gamescores.find()
  db.gamescores.getIndexes()
  ```
  > Type `exit` to get out of MongoDB shell

## Shutdown

- To stop `game-score-api`, go to the terminal where it is running and press `Ctrl+C`
- To stop and remove docker-compose containers, network and volumes, go to a terminal and, inside `springboot-jsoup-html-parser` root folder, run the following command
  ```
  docker-compose down -v
  ```

## Running Tests

Both `game-score-api` and `game-score-collector` have a set of test cases. In order to run them

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder
- Execute the following command (it's not working, see [issues](#issues))
  ```
  ./mvnw clean test --projects game-score-api,game-score-collector
  ```

## Issues

- In `game-score-collector`, the annotation `@EnableMongoAuditing` is commented out due to the following exception at runtime. I've created this [issue #445](https://github.com/spring-projects-experimental/spring-native/issues/445)

- After building and starting successfully the native Docker images, the following exception is thrown when trying to access Swagger
  ```
  SEVERE: Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Handler dispatch failed; nested exception is com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.web.bind.annotation.RestController, interface org.springframework.core.annotation.SynthesizedAnnotation] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.] with root cause
  com.oracle.svm.core.jdk.UnsupportedFeatureError: Proxy class defined by interfaces [interface org.springframework.web.bind.annotation.RestController, interface org.springframework.core.annotation.SynthesizedAnnotation] not found. Generating proxy classes at runtime is not supported. Proxy classes need to be defined at image build time by specifying the list of interfaces that they implement. To define proxy classes use -H:DynamicProxyConfigurationFiles=<comma-separated-config-files> and -H:DynamicProxyConfigurationResources=<comma-separated-config-resources> options.
  	at com.oracle.svm.core.util.VMError.unsupportedFeature(VMError.java:87)
  	at com.oracle.svm.reflect.proxy.DynamicProxySupport.getProxyClass(DynamicProxySupport.java:113)
  	at java.lang.reflect.Proxy.getProxyConstructor(Proxy.java:66)
  	at java.lang.reflect.Proxy.newProxyInstance(Proxy.java:1006)
  	at org.springframework.core.annotation.SynthesizedMergedAnnotationInvocationHandler.createProxy(SynthesizedMergedAnnotationInvocationHandler.java:271)
  	at org.springframework.core.annotation.TypeMappedAnnotation.createSynthesized(TypeMappedAnnotation.java:335)
  	at org.springframework.core.annotation.AbstractMergedAnnotation.synthesize(AbstractMergedAnnotation.java:210)
  	at org.springframework.core.annotation.AbstractMergedAnnotation.synthesize(AbstractMergedAnnotation.java:200)
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.findAnnotationOnBean(DefaultListableBeanFactory.java:734)
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForAnnotation(DefaultListableBeanFactory.java:703)
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeansWithAnnotation(DefaultListableBeanFactory.java:717)
  	at org.springframework.context.support.AbstractApplicationContext.getBeansWithAnnotation(AbstractApplicationContext.java:1322)
  	at org.springdoc.core.OpenAPIService.build(OpenAPIService.java:216)
  	at org.springdoc.api.AbstractOpenApiResource.getOpenApi(AbstractOpenApiResource.java:272)
  	at org.springdoc.webmvc.api.OpenApiResource.openapiJson(OpenApiResource.java:176)
  	at org.springdoc.webmvc.api.OpenApiWebMvcResource.openapiJson(OpenApiWebMvcResource.java:116)
  	at org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource.openapiJson(MultipleOpenApiWebMvcResource.java:93)
  	at java.lang.reflect.Method.invoke(Method.java:566)
  	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:197)
  	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:141)
  	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)
  	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:894)
  	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808)
  	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
  	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1063)
  	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:963)
  	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)
  	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)
  	at javax.servlet.http.HttpServlet.service(HttpServlet.java:626)
  	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)
  	at javax.servlet.http.HttpServlet.service(HttpServlet.java:733)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)
  	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)
  	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.springframework.boot.actuate.metrics.web.servlet.WebMvcMetricsFilter.doFilterInternal(WebMvcMetricsFilter.java:96)
  	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)
  	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)
  	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
  	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
  	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)
  	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
  	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:542)
  	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:143)
  	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
  	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
  	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:357)
  	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:374)
  	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
  	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:893)
  	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1707)
  	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
  	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
  	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
  	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
  	at java.lang.Thread.run(Thread.java:829)
  	at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:553)
  	at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
  ```
