## Usage

env

```shell
export DB_DATABASE=example
export DB_URL="jdbc:mysql://0.0.0.0/${DB_DATABASE}?useSSL=false&allowPublicKeyRetrieval=true"
export DB_USER=root
export DB_PASSWORD=root
```

migrate

```shell
make db-migrate

```

run

```shell
sbt apiHttp/run
```

## Example

### http://localhost:8080/example

```shell
curl -v -X GET http://localhost:8080/example \
  -H "Content-Type: application/json" \
   -d '{"name":"exampple", "age": 10}'
```

### http://localhost:8080/example/http-run

```shell
curl -v -X GET http://localhost:8080/example/http-run \
  -H "Content-Type: application/json"
```

```shell
curl -v -X GET http://localhost:8080/example/from-db \
  -H "Content-Type: application/json"
```

### http://localhost:8080/aaa

```shell
curl -v -X GET http://localhost:8080/aaa
```
