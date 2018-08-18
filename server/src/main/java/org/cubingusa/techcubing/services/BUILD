java_library(
    name = "TechCubingServiceImpl",
    srcs = ["TechCubingServiceImpl.java"],
    visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
    deps = [
        "@io_grpc_grpc_java//stub",
        ":ListPersonsImpl",
        ":GetByIdImpl",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/services:get_by_id_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/services:techcubing_service_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/services:techcubing_service_java_grpc",
    ],
)

java_library(
    name = "ListPersonsImpl",
    srcs = ["ListPersonsImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/org/cubingusa/techcubing/framework:ProtoDb",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/wcif:person_java_proto",
    ],
)

java_library(
    name = "GetByIdImpl",
    srcs = ["GetByIdImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/org/cubingusa/techcubing/framework:ProtoDb",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/services:get_by_id_java_proto",
    ],
)
