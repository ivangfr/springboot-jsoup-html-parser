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
  ./mvnw clean package spring-boot:run --projects game-score-collector -DskipTests \
    -Dspring-boot.run.jvmArguments="-Dspring.data.mongodb.username=gamescoreuser -Dspring.data.mongodb.password=gamescorepass"
  ```
  `game-score-collector` is a Java application that does its job and terminates. Ideally, it will be executed as a cronjob, scheduled to run during specific time intervals.

- Execute the command below to run `game-score-api`
  ```
  ./mvnw clean package spring-boot:run --projects game-score-api -DskipTests \
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
  - Native (it's not working yet for `game-score-api`, see [Issues](#issues))
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

### Start Docker Container

- In a terminal, execute the command below to run `game-score-collector` Docker container
  ```
  docker run --rm \
    --name game-score-collector \
    --network=springboot-jsoup-html-parser_default \
    --env MONGODB_HOST=mongodb \
    --env SPRING_DATA_MONGODB_USERNAME=gamescoreuser \
    --env SPRING_DATA_MONGODB_PASSWORD=gamescorepass \
    docker.mycompany.com/game-score-collector:1.0.0
  ```

- Then, execute the following command to run `game-score-api` Docker container in detached mode
  ```
  docker run -d --rm -p 8080:8080 \
    --name game-score-api \
    --network=springboot-jsoup-html-parser_default \
    --env MONGODB_HOST=mongodb \
    --env SPRING_DATA_MONGODB_USERNAME=gamescoreuser \
    --env SPRING_DATA_MONGODB_PASSWORD=gamescorepass \
    docker.mycompany.com/game-score-api:1.0.0
  ```

## Testing

- The following `curl` command returns the game score results with pagination: page 0, size 10, sorted descending by `score` field.
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

- Stop `game-score-api`
  - If it was started with `Maven`, go to the terminal where it is running and press `Ctrl+C`
  - If it was started as a Docker container, run the following command in a terminal
    ```
    docker stop game-score-api
    ```

- Stop and remove docker-compose containers, network and volumes by running the command below. Make sure you are in `springboot-jsoup-html-parser` root folder
  ```
  docker-compose down -v
  ```

## Running Tests

Both `game-score-api` and `game-score-collector` have a set of test cases. In order to run them

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder

- Execute the following command
  ```
  ./mvnw clean test
  ```

## Issues

- The `game-score-api` docker native image builds successfully but, an exception is thrown at runtime. It's related to `springdoc-openapi`.
  ```
  ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed
  
  java.lang.IllegalStateException: Error processing condition on org.springdoc.core.SpringDocConfiguration.springdocBeanFactoryPostProcessor
  	at org.springframework.boot.autoconfigure.condition.SpringBootCondition.matches(SpringBootCondition.java:60) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.context.annotation.ConditionEvaluator.shouldSkip(ConditionEvaluator.java:108) ~[na:na]
  	at org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.loadBeanDefinitionsForBeanMethod(ConfigurationClassBeanDefinitionReader.java:193) ~[na:na]
  	at org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.loadBeanDefinitionsForConfigurationClass(ConfigurationClassBeanDefinitionReader.java:153) ~[na:na]
  	at org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.loadBeanDefinitions(ConfigurationClassBeanDefinitionReader.java:129) ~[na:na]
  	at org.springframework.context.annotation.ConfigurationClassPostProcessor.processConfigBeanDefinitions(ConfigurationClassPostProcessor.java:343) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:5.3.6]
  	at org.springframework.context.annotation.ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(ConfigurationClassPostProcessor.java:247) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:5.3.6]
  	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:311) ~[na:na]
  	at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:112) ~[na:na]
  	at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:746) ~[na:na]
  	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:564) ~[na:na]
  	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:144) ~[na:na]
  	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:782) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:774) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:439) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:339) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1340) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1329) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at com.mycompany.gamescoreapi.GameScoreApiApplication.main(GameScoreApiApplication.java:13) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  Caused by: java.lang.IllegalStateException: java.io.FileNotFoundException: class path resource [org/springdoc/core/CacheOrGroupedOpenApiCondition$OnCacheDisabled.class] cannot be opened because it does not exist
  	at org.springframework.boot.autoconfigure.condition.AbstractNestedCondition$MemberConditions.getMetadata(AbstractNestedCondition.java:149) ~[na:na]
  	at org.springframework.boot.autoconfigure.condition.AbstractNestedCondition$MemberConditions.getMemberConditions(AbstractNestedCondition.java:121) ~[na:na]
  	at org.springframework.boot.autoconfigure.condition.AbstractNestedCondition$MemberConditions.<init>(AbstractNestedCondition.java:114) ~[na:na]
  	at org.springframework.boot.autoconfigure.condition.AbstractNestedCondition.getMatchOutcome(AbstractNestedCondition.java:62) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	at org.springframework.boot.autoconfigure.condition.SpringBootCondition.matches(SpringBootCondition.java:47) ~[com.mycompany.gamescoreapi.GameScoreApiApplication:na]
  	... 18 common frames omitted
  Caused by: java.io.FileNotFoundException: class path resource [org/springdoc/core/CacheOrGroupedOpenApiCondition$OnCacheDisabled.class] cannot be opened because it does not exist
  	at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:187) ~[na:na]
  	at org.springframework.core.type.classreading.SimpleMetadataReader.getClassReader(SimpleMetadataReader.java:55) ~[na:na]
  	at org.springframework.core.type.classreading.SimpleMetadataReader.<init>(SimpleMetadataReader.java:49) ~[na:na]
  	at org.springframework.core.type.classreading.SimpleMetadataReaderFactory.getMetadataReader(SimpleMetadataReaderFactory.java:103) ~[na:na]
  	at org.springframework.core.type.classreading.SimpleMetadataReaderFactory.getMetadataReader(SimpleMetadataReaderFactory.java:81) ~[na:na]
  	at org.springframework.boot.autoconfigure.condition.AbstractNestedCondition$MemberConditions.getMetadata(AbstractNestedCondition.java:146) ~[na:na]
  	... 22 common frames omitted
  ```

- In `game-score-collector`, the annotation `@EnableMongoAuditing` is commented out due to the following exception at runtime. I've created this [issue #445](https://github.com/spring-projects-experimental/spring-native/issues/445)
  ```
  ERROR 1 --- [           main] o.s.boot.SpringApplication               : Application run failed
  
  org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'websiteCollector' defined in class path resource [com/mycompany/gamescorecollector/collector/WebsiteCollector.class]: Unsatisfied dependency expressed through constructor parameter 3; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'gameScoreServiceImpl' defined in class path resource [com/mycompany/gamescorecollector/service/GameScoreServiceImpl.class]: Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'gameScoreRepository' defined in com.mycompany.gamescorecollector.repository.GameScoreRepository defined in @EnableMongoRepositories declared on MongoRepositoriesRegistrar.EnableMongoRepositoriesConfiguration: Cannot resolve reference to bean 'mongoTemplate' while setting bean property 'mongoOperations'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'mongoTemplate' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Unsatisfied dependency expressed through method 'mongoTemplate' parameter 1; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mappingMongoConverter' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:800) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:229) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1354) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1204) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:564) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:944) ~[na:na]
  	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:918) ~[na:na]
  	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:583) ~[na:na]
  	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:782) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:774) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:439) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:339) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1340) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1329) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  	at com.mycompany.gamescorecollector.GameScoreCollectorApplication.main(GameScoreCollectorApplication.java:12) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:na]
  Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'gameScoreServiceImpl' defined in class path resource [com/mycompany/gamescorecollector/service/GameScoreServiceImpl.class]: Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'gameScoreRepository' defined in com.mycompany.gamescorecollector.repository.GameScoreRepository defined in @EnableMongoRepositories declared on MongoRepositoriesRegistrar.EnableMongoRepositoriesConfiguration: Cannot resolve reference to bean 'mongoTemplate' while setting bean property 'mongoOperations'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'mongoTemplate' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Unsatisfied dependency expressed through method 'mongoTemplate' parameter 1; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mappingMongoConverter' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:800) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:229) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1354) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1204) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:564) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1380) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1300) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:887) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791) ~[na:na]
  	... 19 common frames omitted
  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'gameScoreRepository' defined in com.mycompany.gamescorecollector.repository.GameScoreRepository defined in @EnableMongoRepositories declared on MongoRepositoriesRegistrar.EnableMongoRepositoriesConfiguration: Cannot resolve reference to bean 'mongoTemplate' while setting bean property 'mongoOperations'; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'mongoTemplate' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Unsatisfied dependency expressed through method 'mongoTemplate' parameter 1; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mappingMongoConverter' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:342) ~[na:na]
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:113) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyPropertyValues(AbstractAutowireCapableBeanFactory.java:1689) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1434) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:601) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1380) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1300) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:887) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791) ~[na:na]
  	... 33 common frames omitted
  Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'mongoTemplate' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Unsatisfied dependency expressed through method 'mongoTemplate' parameter 1; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mappingMongoConverter' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:800) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:541) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1334) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1177) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:564) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:330) ~[na:na]
  	... 47 common frames omitted
  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'mappingMongoConverter' defined in class path resource [org/springframework/boot/autoconfigure/data/mongo/MongoDatabaseFactoryDependentConfiguration.class]: Initialization of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:610) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:276) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1380) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1300) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:887) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791) ~[na:na]
  	... 57 common frames omitted
  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.data.mongodb.core.mapping.event.AuditingEntityCallback': Cannot create inner bean '(inner bean)#3c6fec39' of type [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean] while setting constructor argument; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveInnerBean(BeanDefinitionValueResolver.java:389) ~[na:na]
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:134) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.resolveConstructorArguments(ConstructorResolver.java:693) ~[na:na]
  	at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:198) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1354) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1204) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:564) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:335) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:333) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:208) ~[na:na]
  	at org.springframework.beans.factory.support.DefaultListableBeanFactory$1.lambda$stream$0(DefaultListableBeanFactory.java:469) ~[na:na]
  	at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195) ~[na:na]
  	at java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948) ~[na:na]
  	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:484) ~[na:na]
  	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:474) ~[na:na]
  	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150) ~[na:na]
  	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173) ~[na:na]
  	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[na:na]
  	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:497) ~[na:na]
  	at org.springframework.data.mapping.callback.EntityCallbackDiscoverer$CallbackRetriever.discoverEntityCallbacks(EntityCallbackDiscoverer.java:397) ~[na:na]
  	at org.springframework.data.mapping.callback.EntityCallbackDiscoverer.setBeanFactory(EntityCallbackDiscoverer.java:302) ~[na:na]
  	at org.springframework.data.mapping.callback.EntityCallbackDiscoverer.<init>(EntityCallbackDiscoverer.java:71) ~[na:na]
  	at org.springframework.data.mapping.callback.DefaultEntityCallbacks.<init>(DefaultEntityCallbacks.java:59) ~[na:na]
  	at org.springframework.data.mapping.callback.EntityCallbacks.create(EntityCallbacks.java:85) ~[na:na]
  	at org.springframework.data.mongodb.core.convert.MappingMongoConverter.setApplicationContext(MappingMongoConverter.java:225) ~[com.mycompany.gamescorecollector.GameScoreCollectorApplication:3.1.8]
  	at org.springframework.context.support.ApplicationContextAwareProcessor.invokeAwareInterfaces(ApplicationContextAwareProcessor.java:128) ~[na:na]
  	at org.springframework.context.support.ApplicationContextAwareProcessor.postProcessBeforeInitialization(ApplicationContextAwareProcessor.java:102) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(AbstractAutowireCapableBeanFactory.java:422) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1778) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:602) ~[na:na]
  	... 67 common frames omitted
  Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name '(inner bean)#3c6fec39': Instantiation of bean failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1316) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1214) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:564) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:524) ~[na:na]
  	at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveInnerBean(BeanDefinitionValueResolver.java:374) ~[na:na]
  	... 98 common frames omitted
  Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean]: No default constructor found; nested exception is java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:83) ~[na:na]
  	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(AbstractAutowireCapableBeanFactory.java:1308) ~[na:na]
  	... 102 common frames omitted
  Caused by: java.lang.NoSuchMethodException: org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean.<init>()
  	at java.lang.Class.getConstructor0(DynamicHub.java:3349) ~[na:na]
  	at java.lang.Class.getDeclaredConstructor(DynamicHub.java:2553) ~[na:na]
  	at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:78) ~[na:na]
  	... 103 common frames omitted
  ```
