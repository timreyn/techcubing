package(default_visibility = [
    "//src/main/java/com/techcubing/server:__pkg__",
    "//src/main/java/com/techcubing/server/services:__pkg__",
])

load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

# TechCubing Service

proto_library(
    name = "techcubing_service_proto",
    srcs = ["techcubing_service.proto"],
    deps = [
        ":acquire_scorecard_proto",
        ":get_by_id_proto",
        ":list_persons_proto",
        ":release_scorecard_proto",
    ],
)

java_proto_library(
    name = "techcubing_service_java_proto",
    deps = [":techcubing_service_proto"],
)

java_grpc_library(
    name = "techcubing_service_java_grpc",
    srcs = [":techcubing_service_proto"],
    deps = [
        ":acquire_scorecard_java_proto",
        ":get_by_id_java_proto",
        ":list_persons_java_proto",
        ":release_scorecard_java_proto",
        ":techcubing_service_java_proto",
    ],
)

java_grpc_library(
    name = "techcubing_service_java_lite_grpc",
    srcs = [":techcubing_service_proto"],
    flavor = "lite",
    deps = [
        ":lite",
    ],
    visibility = [
        "//src/main/java/com/techcubing/android:__subpackages__",
    ],
)

# ListPersons

proto_library(
    name = "list_persons_proto",
    srcs = ["list_persons.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:request_context_proto",
        "//src/main/java/com/techcubing/proto:response_context_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_proto",
    ],
)
java_proto_library(
    name = "list_persons_java_proto",
    deps = [":list_persons_proto"],
)

# GetById

proto_library(
    name = "get_by_id_proto",
    srcs = ["get_by_id.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:request_context_proto",
        "//src/main/java/com/techcubing/proto:response_context_proto",
        "@com_google_protobuf//:any_proto",
    ],
)
java_proto_library(
    name = "get_by_id_java_proto",
    deps = [":get_by_id_proto"],
)

# AcquireScorecard

proto_library(
    name = "acquire_scorecard_proto",
    srcs = ["acquire_scorecard.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
        "//src/main/java/com/techcubing/proto:request_context_proto",
        "//src/main/java/com/techcubing/proto:response_context_proto",
        "//src/main/java/com/techcubing/proto:scorecard_proto",
    ],
)
java_proto_library(
    name = "acquire_scorecard_java_proto",
    deps = [":acquire_scorecard_proto"],
)

# ReleaseScorecard

proto_library(
    name = "release_scorecard_proto",
    srcs = ["release_scorecard.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
        "//src/main/java/com/techcubing/proto:request_context_proto",
        "//src/main/java/com/techcubing/proto:response_context_proto",
        "//src/main/java/com/techcubing/proto:scorecard_proto",
    ],
)
java_proto_library(
    name = "release_scorecard_java_proto",
    deps = [":release_scorecard_proto"],
)

# lite proto library for android

java_lite_proto_library(
    name = "lite",
    deps = [
        ":acquire_scorecard_proto",
        ":get_by_id_proto",
        ":list_persons_proto",
        ":release_scorecard_proto",
        ":techcubing_service_proto",
    ],
    visibility = [
        "//src/main/java/com/techcubing/android:__subpackages__",
    ],
)
