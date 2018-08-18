java_library(
    name = "TechCubingServiceImpl",
    srcs = ["TechCubingServiceImpl.java"],
    visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
    deps = [
        "@io_grpc_grpc_java//stub",
        ":ListPersonsImpl",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/services:techcubing_service_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/services:techcubing_service_java_grpc",
    ],
)

java_library(
    name = "ListPersonsImpl",
    srcs = ["ListPersonsImpl.java"],
    deps = [
        "//src/main/java/org/cubingusa/techcubing/framework:MysqlConnection",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/wcif:person_java_proto",
    ],
)

