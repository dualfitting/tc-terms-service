# Topcoder - Terms Service

## Prerequesits

- Java 8 with Maven 3
- Docker and Docker Compose

## Configuration

The configuration is done via environment variables, please check `local/run.sh` for reference, basically, you just need to update the `$IP` if you are running the docker services separately in another machine.

# Local Setup For Dependent Services

Basically you need to start the following services:
```
# start the direct-app, this microserivce depends upon the databases in direct-app(see https://github.com/appirio-tech/tc-common-tutorials/tree/master/docker/direct-app)
# and you will also need to create terms in the database of direct-app for local testing
docker-compose up tc-direct

```

You will also need to add the following entry to hosts file:
```
<docker-box-ip> cockpit.cloud.topcoder.com tcs.cloud.topcoder.com
```
Where <docker-box-ip> is the ip address of your docker box. It's 192.168.99.100 for my Windows and Mac docker box, and it should be 127.0.0.1 for linux docker box.

## Start Microservice

Build And Package:

```
mvn clean compile package
```

Then start the service using the following command in the 'local' folder:

```
./run.sh
```

You may use git bash under windows to run shell script.

## Verification

you can use the Postman collections to `docs/` directory to test with the APIs.  
