# TechCubing

This is an experiment to replace physical scorecards in WCA competitions with digital scorecards.  It's still a work in progress.

## Instructions to run the server

1. Clone this repository including submodules:

```sh
$ git clone --recurse-submodules https://github.com/timreyn/techcubing.git
```

2. Install Java 8:

```sh
$ sudo apt install openjdk-8-jdk
```

3. Install [Bazel](https://bazel.build), following the instructions on the [Bazel page](https://docs.bazel.build/versions/master/install-ubuntu.html).

4. Set up MySQL, and run the following commands:
```mysql
> CREATE DATABASE techcubing;
> GRANT ALL PRIVILEGES ON techcubing.* TO 'techcubing' IDENTIFIED BY 'techcubing';
```

5. Build and run the server:

```sh
$ bazel build src/main/java/com/techcubing/server:main
$ bazel-bin/src/main/java/com/techcubing/server/main --dev
```

6. Visit https://localhost:8118/ in your browser, and select a competition.

Note that only competitions starting one month in the past, and delegated or organized by you, will appear.  If you would like to switch competitions, you can visit <https://localhost:8118/set_competition?id=COMPETITION_ID>.

You can also add the flag `--wca=staging` to use the Staging WCA website.

## Instructions to run the app

1. Write the code for the app.

2. Run the app.

## Useful tools

### grpc_cli

TechCubing uses [gRPC](https://grpc.io) to communicate between the app and the server.  The `grpc_cli` tool allows you to send gRPC requests on the command line.  You can follow the instructions [here](https://github.com/grpc/grpc/blob/master/doc/command_line_tool.md) to install the tool.

The service protocol is defined in

```
src/main/java/com/techcubing/proto/service/techcubing_service.proto
```

Sample command (note that you must set the competition ID before running this command):

```sh
$ grpc_cli call localhost:8119 TechCubingService.ListCompetitors ""
``` 
