java_library(
    name = "TechCubingServiceImpl",
    srcs = ["TechCubingServiceImpl.java"],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
    deps = [
        ":AcquireDeviceImpl",
        ":AcquireScorecardImpl",
        ":GetByIdImpl",
        ":GetScrambleImpl",
        ":ListPersonsImpl",
        ":ReleaseDeviceImpl",
        ":ReleaseScorecardImpl",
        "//src/main/proto/services:acquire_device_java_proto",
        "//src/main/proto/services:acquire_scorecard_java_proto",
        "//src/main/proto/services:get_by_id_java_proto",
        "//src/main/proto/services:get_scramble_java_proto",
        "//src/main/proto/services:list_persons_java_proto",
        "//src/main/proto/services:release_device_java_proto",
        "//src/main/proto/services:release_scorecard_java_proto",
        "//src/main/proto/services:techcubing_service_java_grpc",
        "//src/main/proto/services:techcubing_service_java_proto",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@io_grpc_grpc_java//stub",
    ],
)

java_library(
    name = "ListPersonsImpl",
    srcs = ["ListPersonsImpl.java"],
    deps = [
        "//src/main/proto/services:list_persons_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "GetByIdImpl",
    srcs = ["GetByIdImpl.java"],
    deps = [
        "//src/main/proto:options_java_proto",
        "//src/main/proto/services:get_by_id_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "GetScrambleImpl",
    srcs = ["GetScrambleImpl.java"],
    deps = [
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/proto:scramble_java_proto",
        "//src/main/proto/services:get_scramble_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:Puzzle",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "AcquireScorecardImpl",
    srcs = ["AcquireScorecardImpl.java"],
    deps = [
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto:scramble_java_proto",
        "//src/main/proto/services:acquire_scorecard_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "ReleaseScorecardImpl",
    srcs = ["ReleaseScorecardImpl.java"],
    deps = [
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto/services:release_scorecard_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "AcquireDeviceImpl",
    srcs = ["AcquireDeviceImpl.java"],
    deps = [
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/proto:device_java_proto",
        "//src/main/proto/services:acquire_device_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@com_googlecode_json_simple_json_simple//jar",
    ],
)

java_library(
    name = "ReleaseDeviceImpl",
    srcs = ["ReleaseDeviceImpl.java"],
    deps = [
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/proto:device_java_proto",
        "//src/main/proto/services:release_device_java_proto",
        "@com_google_protobuf//:protobuf_java",
    ],
)
