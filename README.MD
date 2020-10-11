# Simple HTTP Webserver

DEMO PROJECT - DO NOT USE IN PRODUCTION

The project runs a simple HTTP webserver to serve static files from a directory.

## Configuration

Environment variables can be passed with the -e flag, all project configuration in 'src/main/resources/application.properties' can be overridden.

## Maven

To build and run the project locally, use `mvn clean install` and `java -jar target/java -jar target/webserver-0.0.1-SNAPSHOT.jar`. Java JDK 11 or higher required.

## Docker

To build the project, run from the project root:

`docker build -t lemberski/webserver .`

Now you can your image:

`docker run -p 8080:8080 lemberski/webserver`

If you want to serve files from a local directory on your machine, say on '~/tmp/webserver_public', use the Docker run command

`docker run -p 8080:8080 -v ~/tmp/webserver_public:/local_www_root_dir -e www.root.dir=/local_www_root_dir lemberski/webserver`

## Loadtest

To run a simple load test, start the app (see 'Maven' and 'Docker' sections), then go to the loadtest directory and run

`npm install`
`npm start`

Configuration options for load testing see loadtest/config.yml. NodeJS is required to run 'npm'.
