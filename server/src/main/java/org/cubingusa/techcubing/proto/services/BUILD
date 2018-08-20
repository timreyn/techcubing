package(default_visibility = ["//visibility:public"])

load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

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
        ":techcubing_service_java_proto",
        ":release_scorecard_java_proto",
    ],
)

proto_library(
    name = "list_persons_proto",
    srcs = ["list_persons.proto"],
    deps = [
        "//src/main/java/org/cubingusa/techcubing/proto:request_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:response_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto/wcif:person_proto",
    ],
)

java_proto_library(
    name = "list_persons_java_proto",
    deps = [":list_persons_proto"],
)

proto_library(
    name = "get_by_id_proto",
    srcs = ["get_by_id.proto"],
    deps = [
        "@com_google_protobuf//:any_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:request_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:response_context_proto",
    ],
)

java_proto_library(
    name = "get_by_id_java_proto",
    deps = [":get_by_id_proto"],
)

proto_library(
    name = "acquire_scorecard_proto",
    srcs = ["acquire_scorecard.proto"],
    deps = [
        "//src/main/java/org/cubingusa/techcubing/proto:request_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:response_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:scorecard_proto",
    ],
)

java_proto_library(
    name = "acquire_scorecard_java_proto",
    deps = [":acquire_scorecard_proto"],
)

proto_library(
    name = "release_scorecard_proto",
    srcs = ["release_scorecard.proto"],
    deps = [
        "//src/main/java/org/cubingusa/techcubing/proto:request_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:response_context_proto",
        "//src/main/java/org/cubingusa/techcubing/proto:scorecard_proto",
    ],
)

java_proto_library(
    name = "release_scorecard_java_proto",
    deps = [":release_scorecard_proto"],
)
