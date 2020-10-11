# Simple HTTP Webserver

The project runs a simple HTTP webserver to serve static files from a directory.

## Docker

To build the project, run from the project root:

`docker build -t lemberski/webserver .`

Now you can run it with

`docker run -p 8080:8080 lemberski/webserver`

Environment variables can be passed with the -e flag. All project configuration in sr/main/resources/application.properties can be overridden.

If you want to serve files from a local directory on your machine, say on '/Users/nik/tmp/webserver_public', use the Docker run command

`docker run -p 8080:8080 -v /Users/nik/tmp/webserver_public:/local_www_root_dir -e www.root.dir=/local_www_root_dir lemberski/webserver`