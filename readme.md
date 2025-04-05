## Movie Application
This application is written with Spring boot (Java 21). It is a restful api to keep track of our movies database.
It tracks the movie, genre and user database. Besides it keeps track of rankings given by user to certain movies and it always is recommend to users similar movies to his liking.


### Features
* Retrieve a list of all movies belonging to a genre.
* Retrieve a list of all movies having an average rating above/below a certain value.
* Retrieve a list of user's interactions to movies (rating or view percentage).
* Add a new user's interaction to movie (rating or view percentage) .
* Recommend a list of movies to a user based on their preferences and history of interactions.
* Search for a list of movies matching a certain genre or a keyword in movie title.

### Technology Stack
![My Skills](https://skillicons.dev/icons?i=java,spring,maven,postgres,linux,docker,git,prometheus,grafana )

### Database
For production the application is using Postgres, while it automatically switches to H2 database when running the test cases

### Database Schema
Overview of Tables and Relationships:

* Users: Stores information about users, with a unique id and a username.
* Movies: Contains details about movies, each identified by a unique id and having a title.
* Genres: Lists different movie genres, each with a unique id and a descriptive name.
* Ranking: Records user rankings for movies. It includes a unique id, the rating given, the number of views, and foreign keys user_id referencing the Users table (indicating who made the ranking) and movie_id referencing the Movies table (specifying which movie was ranked).
* Movie_Genre: This is a many-to-many relationship table linking Movies to Genres. It contains foreign keys movie_id referencing the Movies table and genre_id referencing the Genres table, allowing a movie to belong to multiple genres and a genre to have multiple movies.

This schema provides the foundation for storing user data, movie information, genre classifications, and user-generated rankings, along with the relationships between these entities.

![Alt Database Schema](public/database/database-schema.png)

### Getting Started

#### Prerequisites
Before you start building or running the REST API using Spring Boot, make sure you have the following installed and set up:

1. Java Development Kit (JDK):
   Version Java 21 or higher (depending on your Spring Boot version).
   Check this step by running `java -version` in your terminal.

2. Maven: Used for project build and dependency management. Check this by running `mvn -v`.

3. Spring Boot:
   You don't need to install Spring Boot manually — it's managed by Maven. Use Spring Iniliazer to bootstrap your project.

4. IDE: For example IntelliJ IDEA (preferred with Spring plugin).

#### Installation
1- Clone the repository
``` console
git clone 
```
2- Ensure docker service is running
``` console
docker --version
docker info
```
3- Run the docker compose file
``` console
docker compose up
```
4- Check all docker containers are up and running
```console
docker ps
```
You should see the following docker containers running:
* postgres
* spring-boot
* prometheus
* grafana

### API specification
This project utilizes Swagger (OpenAPI) to document and provide an interactive interface for exploring the application's API. 
You can access the live API specification and 
try out the endpoints directly through the Swagger UI at the following link:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

This interface allows you to view available endpoints, their parameters, request and response structures, and even make test calls to the API without writing any code. 
It's a valuable tool for both development and understanding how to interact with 
the application programmatically. The API specification is automatically generated based on annotations within the Spring Boot application code, ensuring it stays up-to-date with the implemented endpoints.
### Prometheus
This application relies on data collected by Prometheus. To enable and modify Prometheus to scrape metrics from your Spring Boot application, check the yaml file in `data/prometheus/config/prometheus.yaml`
It has a job to target Spring Boot application's `/actuator/prometheus` endpoint. For example, if Spring Boot app runs on `localhost:8080` it uses `localhost:8080/actuator/prometheus`

![Alt Prometheus](public/prometheus/prometheus.png)

### Grafana
This project includes a Grafana dashboard for visualizing application performance, endpoints monitoring and analytics 
You can access the Grafana instance running locally at:

[http://localhost:3000](http://localhost:3000)


**Default Credentials**

* **User:** admin
* **Password:** admin

**Setup Notes:**

* The data source for this dashboard is Prometheus running on port 9090 pulling the data from our Spring Boot Application.
* The dashboard definition can be found in `public/grafana/dashboard.json`. You can import this JSON into your local Grafana instance if needed.
![Alt Grafana](public/grafana/grafana.png)