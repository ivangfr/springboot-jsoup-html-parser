# `game-score-project`

The goal of this project is to get a list of games and their scores from a website. The application must parse the
website HTML content, get the necessary information, save the data in a database and expose them through a
REST API.

# Microservices

The project consists of two microservices: `game-score-collector` and `game-score-api`.

![project-diagram](images/project-diagram.png)

## game-score-api

Spring-boot Java Web application that exposes a REST API from where clients can retrieve the game score data stored in
`MongoDB` database.

| Endpoints                 | Description   |
| ------------------------- | ------------- |
| `GET /api/games`          | this endpoint returns all game score                |
| `GET /api/pg_games`       | this endpoint returns all game score with pagination|
| `GET /api/games/{title}`  | this endpoint returns a specific game score         |

## game-score-collector

Spring-boot Java application responsible for calling the game score website, parse the HTML and save the data in
[`MongoDB`](https://www.mongodb.com/) database. It will be configured to run from time to time in order to keep
the application updated about the information the website provides. 

# Execution Types

There are two types of execution, `Development` and `Production`. The first uses [`Docker`](https://www.docker.com/),
[`docker-compose`](https://docs.docker.com/compose/) and [`Maven`](https://maven.apache.org/). The second needs:
[`Docker`](https://www.docker.com/), [`Minikube`](https://kubernetes.io/docs/tasks/tools/install-minikube/#install-minikube),
[`Helm`](https://helm.sh/docs/using_helm/#installing-the-helm-client) and [`kubectl`](https://kubernetes.io/docs/reference/kubectl/kubectl/)

> *Note*. We are not going to explain how to install all of them because there are many good tutorial available on the internet.

## Development

During development, it is easy to just run the application instead of always build the docker image before running it.

### Start environment

- Open one terminal

- In `game-score-project` root folder run the command below. It will start a `MongoDB` container at port `27017`
```
docker-compose up -d
```

### Start microservices

- Run `game-score-api`. It is Java Web application.
```
./mvnw spring-boot:run --projects game-score-api
```

- Run `game-score-collector`. It is a Java application that does the job and terminates. Ideally, it will be executed
as a cronjob, scheduled to be run during specific time intervals.
```
./mvnw spring-boot:run --projects game-score-collector
```

### Simulation

- In order to test the application, you can access `game-score-api` swagger website: http://localhost:8080

- Or you can use `curl`. For example, the command below returns the game score results with pagination
```
curl -i http://localhost:8080/api/pg_games
```

### Shutdown

To stop and remove containers, networks and volumes
```
docker-compose down -v
```

## Production

### Start environment

- Open one terminal

- Start `Minikube`
```
minikube start
```

- Use `Minikube` Docker Daemon. Instead of pushing the docker image to Docker Registry, we will simply build the image
using the `Minikube` Docker daemon. For it, run the command below to set `Minikube` host.
```
eval $(minikube docker-env)
```
> When `Minikube` host won't be used anymore, you can undo this change by running   
> ```
> eval $(minikube docker-env -u)
> ```

- Inside `game-score-project` root folder, run the following commands to build the docker images of `game-score-api`
and `game-score-collector`.

**game-score-api**
```
./mvnw clean package dockerfile:build -DskipTests --projects game-score-api
```
| Environment Variable | Description |
| -------------------- | ------------- |
| `MONGODB_HOST` | Specify host of the `Mongo` database to use (default `localhost`) |
| `MONGODB_PORT` | Specify port of the `Mongo` database to use (default `27017`) |

**game-score-collector**
```
./mvnw clean package dockerfile:build -DskipTests --projects game-score-collector
```
| Environment Variable | Description |
| -------------------- | ------------- |
| `MONGODB_HOST` | Specify host of the `Mongo` database to use (default `localhost`) |
| `MONGODB_PORT` | Specify port of the `Mongo` database to use (default `27017`) |

### Deployment

- Still inside `game-score-project` root folder, run the following script
```
./deploy-all.sh
```
> *Note*. To check the progress of the deployment run
> ```
> kubectl get pods,cronjobs,jobs
> ```

The deployment will install `MongoDB`, run the job `game-score-collector-job` to get data from website for
the first time, deploy `game-score-api` and, finally, deploy the cronjob `game-score-collector-cronjob` that will run
every `hh:00, hh:10, hh:20, hh:30, hh:40 and hh:50` to get updated data from website.

### Simulation

- In order to test the application, run the following command to get the `game-score-api` url
```
GAME_SCORE_API=$(minikube service game-score-api-service --url)
echo $GAME_SCORE_API
```

- Copy the url shown above and paste in a browser. It will open the`game-score-api` swagger website.

- Or you can use `curl`. For example, the command below returns the game score results with pagination
```
curl -i $GAME_SCORE_API/api/pg_games
```

### Shutdown

- The following script delete all deployments
```
./cleaning-up.sh
```

- The command below shuts down the Minikube Virtual Machine, but preserves all cluster state and data. Starting the
cluster again will restore it to it’s previous state.
```
minikube stop
```

- The command below shuts down and deletes the Minikube Virtual Machine. No data or state is preserved.
```
minikube delete
```
