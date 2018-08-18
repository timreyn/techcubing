package(default_visibility = ["//visibility:public"])

proto_library(
    name = "person_proto",
    srcs = ["person.proto"],
)

java_proto_library(
    name = "person_java_proto",
    deps = [":person_proto"],
)

proto_library(
    name = "device_proto",
    srcs = ["device.proto"],
)

java_proto_library(
    name = "device_java_proto",
    deps = [":device_proto"],
)

proto_library(
    name = "request_context_proto",
    srcs = ["request_context.proto"],
    deps = [":device_proto"],
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
