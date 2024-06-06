## Usage
run 
```shell
sbt apiHttp/run
```

## Example
### http://localhost:8080/example
```shell
curl -v -X GET http://localhost:8080/example\
  -H "Content-Type: application/json" \
   -d '{"name":"exampple", "age": 10}'
```

### http://localhost:8080/aaa
```shell
curl -v -X GET http://localhost:8080/aaa
```