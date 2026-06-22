# Feature Flag Platform

## Why I Started This Project 

In most applications, enabling a new feature usually requires a code deployment. If something goes wrong after release, the feature often has to be rolled back through another deployment.

I wanted to build a centralized Feature Flag Platform that allows applications to enable, disable, and gradually roll out features without requiring code changes or deployments.

The goal is to understand and implement concepts such as:

* Feature toggles
* Percentage-based rollouts
* User targeting
* Caching strategies
* Event-driven updates
* Distributed system design

---

## What is a Feature Flag?

A feature flag is a runtime configuration that decides whether a particular feature should be available to a user.

Instead of:

```java
return newDashboard();
```

applications can use:

```java
if (featureFlagService.isEnabled("NEW_DASHBOARD", userId)) {
    return newDashboard();
}

return oldDashboard();
```

The feature already exists in the application. The Feature Flag Platform simply decides whether the feature should be enabled for a particular user.

---

## Example

Suppose a team wants to release a new dashboard.

Instead of enabling it for everyone at once, they can:

* Release it to internal employees
* Release it to 10% of users
* Increase rollout to 25%
* Increase rollout to 50%
* Enable it for all users

If issues are found, the feature can be disabled immediately without a deployment.

---

## What This Project Will Support

### MVP

* Create feature flags
* Update feature flags
* Delete feature flags
* Evaluate feature flags
* Store configurations in MySQL

### Rollout Engine

* Percentage-based rollout
* User-based targeting
* Rule evaluation

### Performance

* Redis caching
* Low-latency flag evaluation

### Event-Driven Updates

* Kafka-based cache updates
* Audit events
* Configuration synchronization

### Production Features

* Authentication & authorization
* Multi-environment support
* Metrics & monitoring
* Audit logs

---

## High-Level Flow

### Flag Management

```text
Admin
  |
  v
Feature Flag API
  |
  +----> MySQL
  |
  +----> Redis
```

### Flag Evaluation

```text
Application
      |
      v
Evaluate Flag
      |
      v
Redis Cache
      |
      v
Rule Engine
      |
      v
Enabled / Disabled
```

---

## Percentage Rollout

A rollout percentage determines how many users should receive a feature.

Example:

```text
Rollout = 20%
```

For every evaluation:

```text
bucket = hash(userId) % 100
```

If:

```text
bucket < 20
```

the feature is enabled.

Otherwise it remains disabled.

This guarantees that the same user consistently gets the same experience.

---

## Planned Architecture

```text
                    Admin UI
                        |
                        v

              Feature Flag Service
                        |
        +---------------+---------------+
        |                               |
        v                               v

      MySQL                         Redis
        |
        v

       Kafka
        |
        v

  Evaluation Service
        |
        v

   Client Applications
```

---

## Tech Stack

* Java 17
* Spring Boot
* MySQL
* Redis
* Kafka
* Maven

---

## Learning Goals

This project is mainly focused on understanding how systems like LaunchDarkly work internally.

While building it, I want to explore:

* Rule engine design
* Cache consistency
* Event-driven architecture
* Distributed system patterns
* High-throughput read APIs
* Production-ready backend design

---

## How Applications Use This Platform

This platform does not create or own application features. The actual feature implementation lives inside the client application.

For example, suppose an e-commerce application has both the old and new versions of an Offers page:

```java
@GetMapping("/offers")
public String offers(String userId) {

    if(featureFlagClient.isEnabled(
            "NEW_OFFERS_PAGE",
            userId
    )) {
        return "New Offers Page";
    }

    return "Old Offers Page";
}
```

When a request arrives, the application asks the Feature Flag Platform whether the feature should be enabled for the current user.

```text
Client Application
        |
        v
Feature Flag Platform
        |
        v
Evaluate Rules
        |
        v
Enabled / Disabled
```

The platform evaluates:

* Whether the flag is active
* Percentage rollout rules
* User targeting rules
* Environment-specific configurations

and returns a decision to the application.

This allows teams to release features gradually, perform experiments, and disable problematic features without redeploying code.

---

## Demo Setup

To demonstrate the platform end-to-end, this repository will eventually contain two applications:

### 1. Feature Flag Platform

Responsible for:

* Managing feature flags
* Evaluating rollout rules
* Storing configurations
* Publishing configuration updates

### 2. Demo Application

A sample client application that consumes feature flags from the platform.

Example use cases:

* New Dashboard
* New Offers Page
* Premium Features

The demo application will call the platform during runtime and render different behavior depending on the evaluation result.

This setup helps validate real-world rollout scenarios such as:

* 10% user rollout
* Country-based targeting
* Premium user access
* Instant feature rollback

---


## Query
```sql
CREATE TABLE feature_flags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flag_key VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
      ON UPDATE CURRENT_TIMESTAMP
);
```

## API Examples

### Create a Feature Flag

```bash
curl --location 'http://localhost:8080/api/v1/flags/createflag' \
--header 'Content-Type: application/json' \
--data '{
    "flagKey": "NEW_DASHBOARD",
    "name": "New Dashboard Feature",
    "description": "Enable the new dashboard experience",
    "enabled": false
}'
```

#### Response (201 Created)

```json
{
    "id": 1,
    "flagKey": "NEW_DASHBOARD",
    "name": "New Dashboard Feature",
    "description": "Enable the new dashboard experience",
    "enabled": false,
    "active": true,
    "createdAt": "2026-06-13T19:00:00",
    "updatedAt": "2026-06-13T19:00:00"
}
```

---

### Create a Duplicate Feature Flag

```bash
curl --location 'http://localhost:8080/api/v1/flags/createflag' \
--header 'Content-Type: application/json' \
--data '{
    "flagKey": "NEW_DASHBOARD",
    "name": "New Duplicate Feature",
    "description": "Test Duplicate",
    "enabled": false
}'
```

#### Response (409 Conflict)

```json
{
    "timestamp": "2026-06-13T19:05:00",
    "status": 409,
    "message": "Feature flag already exists with key: NEW_DASHBOARD"
}
```

---

### Validation Failure

```bash
curl --location 'http://localhost:8080/api/v1/flags/createflag' \
--header 'Content-Type: application/json' \
--data '{
    "flagKey": "",
    "name": ""
}'
```

#### Response (400 Bad Request)

```json
{
    "timestamp": "2026-06-13T19:06:00",
    "status": 400,
    "message": "Flag key must not be blank"
}
```

### Create a Feature Flag

```bash
curl --location 'http://localhost:8080/api/v1/flags/all'
```

#### Response

```json
[
    {
        "id": 1,
        "flagkey": "NEW_DASHBOARD",
        "name": "New Dashboard Feature",
        "description": "Enable the new dashboard experience",
        "enabled": false,
        "active": true,
        "createdAt": "2026-06-13T18:44:37",
        "updatedAt": "2026-06-13T18:44:37"
    },
    {
        "id": 2,
        "flagkey": "NEW_PAYMENT_FLAG",
        "name": "New Payment Feature",
        "description": "New Payment Feature",
        "enabled": false,
        "active": true,
        "createdAt": "2026-06-13T18:50:11",
        "updatedAt": "2026-06-13T18:50:11"
    }
]
```

### Check a Feature Flag status

```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/status'
```

#### Response for existing flag

```json
{
    "flagkey": "NEW_DASHBOARD",
    "enabled": false
}
```

### Response for non-existing flag
```json
{
    "timestamp": "2026-06-16T21:02:09.649618",
    "status": 404,
    "message": "flag key OLD_DASHBOARD not found"
}
```


### Toggle a Feature Flag status

```bash
curl --location --request PATCH 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/update' \
--header 'Content-Type: application/json' \
--data '{"enabled":true}'
```

#### Response for existing flag

```json
{
    "flagkey": "NEW_DASHBOARD",
    "enabled": true
}
```

### Response for non-existing flag
```json
{
    "timestamp": "2026-06-16T21:27:43.489448",
    "status": 404,
    "message": "flag key OLD_DASHBOARD not found"
}
```


### Evaluate a feature flag
```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/evaluate'
```

### Response for success
```json
{
    "flagKey": "NEW_DASHBOARD",
    "enabled": true,
    "reason": "ENABLED"
}
```
### Failure Response
```json
{
    "timestamp": "2026-06-17T21:44:28.010601",
    "status": 404,
    "message": "flag key W_DASHBOARD not found"
}
```


### Enabling ROllout Percentage
```sql
ALTER TABLE feature_flags
ADD COLUMN rollout_percentage INT NOT NULL DEFAULT 100;
```


### Update Evaluate API
```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/evaluate?userId=Ajay'
```

### Response
```json
{
    "flagKey": "NEW_DASHBOARD",
    "enabled": true,
    "reason": "ROLLOUT_MATCHED",
    "rolloutPercentage": null
}
```

### Rollout Update API

```bash
curl --location --request PATCH 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/rollout' \
--header 'Content-Type: application/json' \
--data '{
    "rolloutPercentage": 30
}'
```

### Success Response with 30 percent rollout
```json
{
    "flagkey": "NEW_DASHBOARD",
    "enabled": true,
    "rolloutPercentage": 30
}
```

### Error Response with 250 percent rollout
```json
{
    "timestamp": "2026-06-17T22:44:29.488392",
    "status": 400,
    "message": "Rollout percentage cannot be greater than 100"
}
```


### Introduction of User Target Rules
```sql
CREATE TABLE feature_flag_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feature_flag_id BIGINT NOT NULL,
    rule_type VARCHAR(50) NOT NULL,
    rule_value VARCHAR(255) NOT NULL,

    FOREIGN KEY (feature_flag_id)
        REFERENCES feature_flags(id)
);
```

### adding rule to flag key
```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/addRule' \
--header 'Content-Type: application/json' \
--data '{
    "ruleType": "USER_ID",
    "ruleValue": "user123"
}'
```

### Response
```json
{
    "id": 1,
    "ruleType": "USER_ID",
    "ruleValue": "user123"
}
```

### Get All Rules
```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/getRules'
```

```json
[
    {
        "id": 1,
        "ruleType": "USER_ID",
        "ruleValue": "user123"
    }
]
```

### Delete Rule
```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/flags/deleteRule/1'
```


### Evaluation logic of feature flag rule
```bash
curl --location 'http://localhost:8080/api/v1/flags/NEW_DASHBOARD/evaluate?userId=user123'
```

```json
{
    "flagKey": "NEW_DASHBOARD",
    "enabled": true,
    "reason": "USER_RULE_MATCHED",
    "rolloutPercentage": null
}
```