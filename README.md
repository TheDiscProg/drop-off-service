# SIMEX Drop-Off Service
Drop-off service for Event Driven Architecture using SIMEX Messaging API

It:
* Is written in Scala
* Uses Guardrail to build the HTTP API
* Uses Ember embedded HTTP server
* Validates SIMEX messages
* Sends SIMEX request to the RMQ

The drop-off service simply receives SIMEX messages and sends them on internally to the respective 
backend services using the `endpoint.resource` in the SIMEX request.

## Message Validation and Security Check
There is some request validation and security check but could be improved by checking for a valid authorisation token.

## Health Check
A basic health check is enabled in this service, but additional health checks can be enabled as described below. An 
high-level description is given below.

HeathRoutes --> HealthCheckService --> List: HealthChecker

**HealthCheckService** returns ***HealthCheckStatus*** which consists of:  
***HealthStatus***: The overall status of the service  
A list of ***HealthCheckerResponse*** each containing the response of a **HealthChecker**.

### Create Specific Health Check
Create a specific checker by implementing the trait **HealthChecker**.

### Add Checker to Checkers list for HealthCheckService
Add the health checker to the list for **HealthCheckService** in **AuthenticatorServer**.

## Creating Docker Image and pushing it to Docker Hub
This project has sbt-native-packager enabled for Docker images. Use:

```
    sbt docker:publishLocal
```
which will install a docker image locally. You can then start it locally exposing port 8002.
It will be automatically tagged with the build version.

To push the image into Docker hub:
```
    docker login
    docker push <repo>/drop-off-service:<version>
```