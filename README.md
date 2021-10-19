# Introduction

A sample project for testing the nginx location and proxy_pass configuration scenario.

# Run with maven

```
mvn spring-boot:run
```

# Run with docker

```
docker run -it --rm --name nlpp -v "$(pwd)":/usr/src/nlpp -v "$HOME/.m2":/root/.m2  -w /usr/src/nlpp maven:3.8.2-openjdk-16 mvn spring-boot:run
```

* **docker run** , Will automatically download the image, create a container and start the container;
* **-it** , The output is displayed on the console, and if input is required, it can be entered through the console;
* **--rm** ,If you stop the container, will remove it automatically;
* **--name nlpp** , the name of the container;
* **-v "$(pwd)":/usr/src/nlpp** , Indicates mapping the current directory (for example: /var/www/projects/nlpp) to the /usr/src/nlpp directory within the container;
* **-w /usr/src/nlpp** , This means that inside the container, /usr/src/nlpp is used as the working directory for running maven, which, together with the -v mapping, actually means that the current directory is used as the working directory for running;
* **-v "$HOME/.m2":/root/.m2** , Mapping the maven cache in the container to the host, avoiding the need to download dependency packages every time the container is started;
* **maven:3.8.2-openjdk-16** , The image will run;
* **mvn spring-boot:run** , The maven command you want to run.

# Run with docker-compose

```
docker-compose up
```

# Configuration Scenario

## http://example.com/a/b/c -> http://proxy.com/a/b/c

* Example

Request URL | Proxy URL
-- | -- |
http://nlpp.futlabs.com/app | http://app:8080/app
http://nlpp.futlabs.com/app/ | http://app:8080/app/
http://nlpp.futlabs.com/app?a=100 | http://app:8080/app?a=100
http://nlpp.futlabs.com/app123 | http://app:8080/app123
http://nlpp.futlabs.com/app/a/b/c | http://app:8080/app/a/b/c

* Example Configuration

```
    location / {
        proxy_pass http://app:8080;
    }
```

## http://example.com/app/a/b/c -> http://proxy.com/a/b/c

* Example

Request URL | Proxy URL
-- | -- |
http://nlpp.futlabs.com/app/ | http://app:8080/
http://nlpp.futlabs.com/app?a=100 | http://app:8080/?a=100
http://nlpp.futlabs.com/app/123 | http://app:8080/123
http://nlpp.futlabs.com/app/a/b/c | http://app:8080/a/b/c

* Example Configuration

```
    location /app/ {
        proxy_pass http://app:8080/;
    }
```

## http://example.com/app1/a/b/c -> http://proxy.com/app2/a/b/c

* Example

Request URL | Proxy URL
-- | -- |
http://nlpp.futlabs.com/app/ | http://app:8080/nlpp/
http://nlpp.futlabs.com/app?a=100 | http://app:8080/nlpp/?a=100
http://nlpp.futlabs.com/app/a/b/c | http://app:8080/nlpp/a/b/c

* Example Configuration

```
    location /app/ {
        proxy_pass http://app:8080/nlpp/;
    }
```

## SEO friendly URL -> Ugly parameters based URL

* Example

Request URL | Proxy URL
-- | -- |
GET http://nlpp.futlabs.com/user/123 | http://app:8080/read/?entity=user&id=123
POST http://nlpp.futlabs.com/user/123 | http://app:8080/update/?entity=user&id=123
POST http://nlpp.futlabs.com/user/123?name=Bill&gender=M | http://app:8080/update/?entity=user&id=123&name=Bill&gender=M
PUT http://nlpp.futlabs.com/user/123 | http://app:8080/update/?entity=user&id=123
POST http://nlpp.futlabs.com/user | http://app:8080/create/?entity=user
POST http://nlpp.futlabs.com/user?name=Bill&gender=M | http://app:8080/create/?entity=user&name=Bill&gender=M
DELETE http://nlpp.futlabs.com/user/123 | http://app:8080/del/?entity=user&id=123
GET http://nlpp.futlabs.com/user/search | http://app:8080/query/?entity=user
GET http://nlpp.futlabs.com/user/search?name=Bill&gender=M | http://app:8080/query/?entity=user&name=Bill&gender=M
POST http://nlpp.futlabs.com/user/search | http://app:8080/query/?entity=user


* Example Configuration

```
    location /user {
        if ($request_method = GET){
            rewrite /user/([0-9]+) /read/?entity=user&id=$1 break;
            rewrite /user/search /query/?entity=user break;
        }
        if ($request_method ~ "POST|PUT" ){
            rewrite /user/([0-9]+) /update/?entity=user&id=$1 break;
            rewrite /user/search /query/?entity=user break;
            rewrite /user /create/?entity=user break;
        }
        if ($request_method = DELETE ){
            rewrite /user/([0-9]+) /del/?entity=user&id=$1 break;
        }
        
        proxy_pass http://app:8080;
    }
```





