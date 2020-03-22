# Akka Http with ZIO example 


### About

Testing ZIO lib with Akka Http server and client

## Run


#### Start SourceServer 

Plain Akka Http echo server with two endpoints:

- status - fails randomly 
- slow - sleeps for over 5 seconds

```
$ sbt "runMain test.zio.SourceServer"
```

#### Akka Http wrapped with ZIO

Adds "powers" to Akka Http client using ZIO, things like retry and timeout...

```
$ sbt "runMain test.zio.HttpServer"
```

#### Curl cli

```
$ curl http://localhost:8080/zio
This is sample response: 1584889779499 

$ curl http://localhost:8080/slow
to many tries, Timeout occurred, interrupted 

OR
$ time for i in {1..10}; do curl http://localhost:8080/zio; done

```