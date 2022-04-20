# springboot-jsoup-html-parser

The goal of this project is to get a list of games and their scores from a website. The application must parse the website HTML content, get the necessary information, save the data in a database and expose them through a REST API.

> **Note:** In [`kubernetes-minikube-environment`](https://github.com/ivangfr/kubernetes-minikube-environment/tree/master/html-parser-job-cronjob) repository, it's shown how to deploy this project in `Kubernetes` (`Minikube`)

## Project Diagram

![project-diagram](documentation/project-diagram.png)

## Applications

- ### game-score-api

  [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java Web application that exposes a REST API from where clients can retrieve the game score data stored in `MongoDB` database.

  | Endpoints                | Description                                                  |
  |--------------------------|--------------------------------------------------------------|
  | `GET /api/games[?title]` | returns all game scores or filtered by title with pagination |
  | `GET /api/games/{id}`    | returns a specific game score filtered by id                 |

- ### game-score-collector

  `Spring Boot` Java application responsible for calling the game score website, parse the HTML content (using [`jsoup`](https://jsoup.org/)) and save the data in [`MongoDB`](https://www.mongodb.com/) database. It will be configured to run from time to time in order to keep the application updated about the information the website provides. 

## Prerequisites

- [`Java 11+`](https://www.oracle.com/java/technologies/downloads/#java11)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)

## Start Environment

- Open a terminal and, inside `springboot-jsoup-html-parser` root folder, the command below
  ```
  docker-compose up -d
  ```

- Wait for `mongodb` Docker container to be up and running. To check it, run
  ```
  docker-compose ps
  ```

## Running Applications with Maven

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder

- Execute the following command to run `game-score-collector`
  ```
  ./mvnw clean spring-boot:run --projects game-score-collector
  ```
  `game-score-collector` is a Java application that does its job and terminates. Ideally, it will be executed as a cronjob, scheduled to run during specific time intervals.

- Execute the command below to run `game-score-api`
  ```
  ./mvnw clean spring-boot:run --projects game-score-api
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
  |----------------------|-------------------------------------------------------------------|
  | `MONGODB_HOST`       | Specify host of the `Mongo` database to use (default `localhost`) |
  | `MONGODB_PORT`       | Specify port of the `Mongo` database to use (default `27017`)     |

- **game-score-collector**

  | Environment Variable | Description                                                       |
  |----------------------|-------------------------------------------------------------------|
  | `MONGODB_HOST`       | Specify host of the `Mongo` database to use (default `localhost`) |
  | `MONGODB_PORT`       | Specify port of the `Mongo` database to use (default `27017`)     |

### Start Docker Containers

- In a terminal, execute the command below to run `game-score-collector` Docker container
  ```
  docker run --rm --name game-score-collector \
    -e MONGODB_HOST=mongodb \
    --network=springboot-jsoup-html-parser_default \
    ivanfranchin/game-score-collector:1.0.0
  ```

- Then, execute the following command to run `game-score-api` Docker container
  ```
  docker run --rm --name game-score-api -p 8080:8080 \
    -e MONGODB_HOST=mongodb \
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
  docker exec -it mongodb mongo gamescoredb
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
- Execute the following command
  ```
  ./mvnw clean test --projects game-score-api,game-score-collector
  ```

## Cleanup

To remove the Docker images created by this project, go to a terminal and, inside `springboot-jsoup-html-parser` root folder, run the following script
```
./remove-docker-images.sh
```
