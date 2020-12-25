# `springboot-jsoup-html-parser`

The goal of this project is to get a list of games and their scores from a website. The application must parse the website HTML content, get the necessary information, save the data in a database and expose them through a REST API.

## Project Diagram

![project-diagram](images/project-diagram.png)

## Applications

- ### game-score-api

  [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Java Web application that exposes a REST API from where clients can retrieve the game score data stored in `MongoDB` database.

  | Endpoints                 | Description                                          |
  | ------------------------- | ---------------------------------------------------- |
  | `GET /api/games`          | this endpoint returns all game score with pagination |
  | `GET /api/games/{title}`  | this endpoint returns a specific game score          |

- ### game-score-collector

  `Spring Boot` Java application responsible for calling the game score website, parse the HTML content (using [`jsoup`](https://jsoup.org/)) and save the data in [`MongoDB`](https://www.mongodb.com/) database. It will be configured to run from time to time in order to keep the application updated about the information the website provides. 

## Execution Types

There are two types of execution, `Development` and `Production`. The first uses [`Docker`](https://www.docker.com/), [`docker-compose`](https://docs.docker.com/compose/) and [`Maven`](https://maven.apache.org/). The second needs: [`Docker`](https://www.docker.com/), [`Minikube`](https://kubernetes.io/docs/tasks/tools/install-minikube/#install-minikube), [`Helm`](https://helm.sh/docs/using_helm/#installing-the-helm-client) and [`kubectl`](https://kubernetes.io/docs/reference/kubectl/kubectl/)

> **Note:** We are not going to explain how to install all of them because there are many good tutorials available on the internet.

## Development

### Start environment

- Open a terminal and navigate to `springboot-jsoup-html-parser` root folder

- Run the command below to start a `MongoDB` docker container at port `27017
  ```
  docker-compose up -d
  ```

### Start Applications

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder
 
- Execute the command below to start `game-score-api`
  ```
  ./mvnw clean spring-boot:run --projects game-score-api \
    -Dspring-boot.run.jvmArguments="-Dspring.data.mongodb.username=gamescoreuser -Dspring.data.mongodb.password=gamescorepass"
  ```

- Open another terminal and make sure you are inside `springboot-jsoup-html-parser` root folder

- Run the following command to start `game-score-collector`
  ```
  ./mvnw spring-boot:run --projects game-score-collector \
    -Dspring-boot.run.jvmArguments="-Dspring.data.mongodb.username=gamescoreuser -Dspring.data.mongodb.password=gamescorepass"
  ```
  `game-score-collector` is a Java application that does its job and terminates. Ideally, it will be executed as a cronjob, scheduled to run during specific time intervals.

### Testing

- In order to test the application, you can access `game-score-api` Swagger website: http://localhost:8080

- Another way to test is using `curl`. For instance, the command below returns the game score results with pagination: page 0, size 10, sorted descending by `score` field.
  ```
  curl -i "http://localhost:8080/api/games?page=0&size=10&sort=score%2Cdesc"
  ```

### Shutdown

- Stop `game-score-api` by going to its terminal and pressing `Ctrl+C`
- Stop and remove `docker-compose` containers, networks and volumes by running the command below
  ```
  docker-compose down -v
  ```

### Running Tests

Both `game-score-api` and `game-score-collector` have a set of test cases. In order to run them

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder

- Execute the following command
  ```
  ./mvnw clean test
  ```

## Production

### Start environment

- Open one terminal and run the command below to start `Minikube`
  ```
  minikube start --memory='8000mb' --vm-driver='virtualbox'
  ```

- Instead of pushing the docker image to Docker Registry, we will simply build the image using the `Minikube` Docker daemon. For it, open a terminal and run the command below to set `Minikube` host
  ```
  eval $(minikube docker-env)
  ```

- Inside `springboot-jsoup-html-parser` root folder, run the following script to build the docker images of `game-score-api` and `game-score-collector`
  ```
  ./build-apps.sh
  ```

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

- As `Minikube` host won't be used anymore, you can undo this change by running
  ```
  eval $(minikube docker-env -u)
  ```

### Deployment

- In a terminal, make sure you are inside `springboot-jsoup-html-parser` root folder

- Run the following script
  ```
  ./deploy-all.sh
  ```
  
  The deployment will:
  - Create `dev` namespace where everything will be installed;
  - Install `MongoDB` helm chart;
  - Run the job `game-score-collector-job` to get data from website for the first time;
  - Deploy `game-score-api`;
  - Deploy `game-score-collector-cronjob` that will run every `hh:00, hh:10, hh:20, hh:30, hh:40 and hh:50` to get updated data from website.
  
- To check the progress of the deployment run
  ```
  kubectl get --namespace dev pods,cronjobs,jobs
  ```

### Testing

- In order to test the application, let's first get the `game-score-api` url by running the following command
  ```
  GAME_SCORE_API=$(minikube service --namespace dev game-score-api-service --url)
  echo $GAME_SCORE_API
  ```

- Copy the url displayed above and paste in a browser. It will open the`game-score-api` Swagger website.

- Another way to test is using `curl`. For instance, the command below returns the game score results with pagination: page 0, size 10, sorted descending by `score` field.
  ```
  curl -i "$GAME_SCORE_API/api/games?page=0&size=10&sort=score%2Cdesc"
  ```

### Shutdown

- The following script delete all deployments
  ```
  ./cleanup.sh
  ```

- The command below shuts down the `Minikube Virtual Machine`, but preserves all cluster state and data. Starting the cluster again will restore it to its previous state.
  ```
  minikube stop
  ```

- The command below shuts down and deletes the `Minikube Virtual Machine`. No data or state is preserved.
  ```
  minikube delete
  ```
