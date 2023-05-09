# DAPEX Drop-Off Service
EDA/DAPEX Drop-Off service.

It:
* Is written in Scala
* Uses Guardrail to build the HTTP API
* Uses Ember embedded HTTP server
* Validates DAPEX messages
* Sends DAPEX message to the RMQ

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