package(default_visibility = ["//visibility:public"])

proto_library(
    name = "options_proto",
    srcs = ["options.proto"],
    deps = [
        "@com_google_protobuf//:descriptor_proto",
    ],
)

java_proto_library(
    name = "options_java_proto",
    deps = [":options_proto"],
)

proto_library(
    name = "device_proto",
    srcs = ["device.proto"],
    deps = [
        ":device_type_proto",
        ":options_proto",
        "@com_google_protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "device_java_proto",
    deps = [":device_proto"],
)

proto_library(
    name = "device_type_proto",
    srcs = ["device_type.proto"],
)

java_proto_library(
    name = "device_type_java_proto",
    deps = [":device_type_proto"],
)

proto_library(
    name = "device_config_proto",
    srcs = ["device_config.proto"],
    deps = [
        ":device_proto",
        "//src/main/java/com/techcubing/proto/wcif:competition_proto",
    ],
)

java_proto_library(
    name = "device_config_java_proto",
    deps = [":device_config_proto"],
)

java_lite_proto_library(
    name = "device_config_java_lite_proto",
    deps = [":device_config_proto"],
)

proto_library(
    name = "request_context_proto",
    srcs = ["request_context.proto"],
    deps = [":options_proto"],
)

java_proto_library(
    name = "request_context_java_proto",
    deps = [":request_context_proto"],
)

proto_library(
    name = "response_context_proto",
    srcs = ["response_context.proto"],
)

java_proto_library(
    name = "response_context_java_proto",
    deps = [":response_context_proto"],
)

proto_library(
    name = "scorecard_proto",
    srcs = ["scorecard.proto"],
    deps = [
        ":device_type_proto",
        ":options_proto",
        "@com_google_protobuf//:timestamp_proto",
    ],
)

java_proto_library(
    name = "scorecard_java_proto",
    deps = [":scorecard_proto"],
)
