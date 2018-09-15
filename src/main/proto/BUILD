package(default_visibility = [
    "//src/main/proto/services:__subpackages__",
    "//src/main/java/com/techcubing/server:__subpackages__",
])

# options.proto

proto_library(
    name = "options_proto",
    srcs = ["options.proto"],
    deps = [
        "@com_google_protobuf//:descriptor_proto",
    ],
    # Proto options are allowed to be set in WCIF.
    visibility = [
        "//src/main/proto:__subpackages__",
    ],
)
java_proto_library(
    name = "options_java_proto",
    deps = [":options_proto"],
)

# device.proto

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

# device_type.proto

proto_library(
    name = "device_type_proto",
    srcs = ["device_type.proto"],
)
java_proto_library(
    name = "device_type_java_proto",
    deps = [":device_type_proto"],
)

# device_config.proto

proto_library(
    name = "device_config_proto",
    srcs = ["device_config.proto"],
    deps = [
        ":device_proto",
        "//src/main/proto/wcif:competition_proto",
    ],
)
java_proto_library(
    name = "device_config_java_proto",
    deps = [":device_config_proto"],
)

# request_context.proto

proto_library(
    name = "request_context_proto",
    srcs = ["request_context.proto"],
    deps = [":options_proto"],
)
java_proto_library(
    name = "request_context_java_proto",
    deps = [":request_context_proto"],
)

# response_context.proto

proto_library(
    name = "response_context_proto",
    srcs = ["response_context.proto"],
)
java_proto_library(
    name = "response_context_java_proto",
    deps = [":response_context_proto"],
)

# scorecard.proto

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

# scramble.proto

proto_library(
    name = "scramble_proto",
    srcs = ["scramble.proto"],
    deps = [
        ":options_proto",
    ],
)
java_proto_library(
    name = "scramble_java_proto",
    deps = [":scramble_proto"],
)

# wca_environment.proto

proto_library(
    name = "wca_environment_proto",
    srcs = ["wca_environment.proto"],
)
java_proto_library(
    name = "wca_environment_java_proto",
    deps = [":wca_environment_proto"],
)
