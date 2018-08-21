java_library(
    name = "TechCubingServiceImpl",
    srcs = ["TechCubingServiceImpl.java"],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
    deps = [
        "@io_grpc_grpc_java//stub",
        ":AcquireScorecardImpl",
        ":GetByIdImpl",
        ":ListPersonsImpl",
        ":ReleaseScorecardImpl",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/proto/services:acquire_scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/services:get_by_id_java_proto",
        "//src/main/java/com/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/com/techcubing/proto/services:release_scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/services:techcubing_service_java_proto",
        "//src/main/java/com/techcubing/proto/services:techcubing_service_java_grpc",
    ],
)

java_library(
    name = "ListPersonsImpl",
    srcs = ["ListPersonsImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/proto/services:list_persons_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_java_proto",
    ],
)

java_library(
    name = "GetByIdImpl",
    srcs = ["GetByIdImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/proto/services:get_by_id_java_proto",
    ],
)

java_library(
    name = "AcquireScorecardImpl",
    srcs = ["AcquireScorecardImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/proto:device_java_proto",
        "//src/main/java/com/techcubing/proto:scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/services:acquire_scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
    ],
)

java_library(
    name = "ReleaseScorecardImpl",
    srcs = ["ReleaseScorecardImpl.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/proto:device_java_proto",
        "//src/main/java/com/techcubing/proto:device_type_java_proto",
        "//src/main/java/com/techcubing/proto:scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/services:release_scorecard_java_proto",
    ],
)
