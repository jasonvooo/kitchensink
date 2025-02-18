# Kitchen Sink Modernisation

Migration
of [JBoss eap quickstart Kitchen Sink](https://github.com/jboss-developer/jboss-eap-quickstarts/tree/8.0.x/kitchensink)

## Architecture

- Java 21
- Quarkus
- MongoDB
- Maven

## How to Run

### Run application locally

```shell
mvn quarkus:dev
```

This will start the application on **http://localhost:9000**

### Test

Unit tests will be run

```shell
mvn test
```