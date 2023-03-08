# spring-boot-pos
This is a POS integrated e-commerce system written in Kotlin (with GraphQL).
The system accept makePayment request from user, and record it in database (PostgreSQL).
The system can also accept user to lookup how much sales were made within a date range.

## Getting Started

### How to Setup
There are two ways to run this project: `maven run` and `docker-compose`
#### Method 1 : `maven run`
##### Requirements
- openjdk 17+
- PostgreSQL

##### Steps
1. Install jdk (version 17+), and make sure env variable `JAVA_HOME` is set to correct path.
```shell
$ echo $JAVA_HOME
```

2. Install and run PostgreSQL database (port: 5432), and create user `posuser` and db `pos` beforehand
   To create user `posuser`:
```shell
$ createuser posuser
```

To create db `pos`:
```shell
$ createdb pos
```

3. Under project root, run maven to start the application
```shell
$ cd spring-boot-pos
$ ./mvnw spring-boot:run
```

4. Have fun with this project by:
- Open web browser and go to `http://localhost:8080/graphiql`
- Send GraphQL query to `localhost:8080/graphql`

#### Method 2 : `docker-compose`
##### Requirements
- Docker (Finish install and everything is setup)

##### Steps
1. Under project root, run `docker compose up` command
```shell
$ docker compose up -d
```

2. Have fun with this project by:
- Open web browser and go to `http://localhost:8080/graphiql`
- Send GraphQL query to `localhost:8080/graphql`

## Sample GraphQL query
Below are some sample query and variables that can test this application:

### Query
```graphql
query SalesQuery ($startDateTime: String!, $endDateTime: String!) {
  sales(startDateTime: $startDateTime, endDateTime: $endDateTime) {
    datetime
    sales
    points
  }
}

mutation payment($paymentRequest: PaymentRequest) {
  makePayment(paymentRequest: $paymentRequest) {
    finalPrice
    points
  }
}
```

### Variables
```json
{
  "startDateTime": "2023-03-06T00:00:00Z",
  "endDateTime": "2023-03-07T00:00:00Z",
  "paymentRequest": {
    "price": "1000.00",
    "priceModifier": 1.00,
    "paymentMethod": "CASH",
    "datetime": "2023-03-06T02:00:00Z"
  }
}
```

## Run Tests
With below command, you can run all the tests in this project
```shell
$ ./mvnw clean verify
```

## Author
Jordan Su <newjordansu1126@gmail.com>