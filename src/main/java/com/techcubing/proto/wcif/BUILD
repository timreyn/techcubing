package(default_visibility = ["//visibility:public"])

proto_library(
    name = "person_proto",
    srcs = ["person.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
    ],
)

java_proto_library(
    name = "person_java_proto",
    deps = [":person_proto"],
)

proto_library(
    name = "competition_proto",
    srcs = ["competition.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_proto",
        "//src/main/java/com/techcubing/proto/wcif:event_proto",
    ],
)

java_proto_library(
    name = "competition_java_proto",
    deps = [":competition_proto"],
)

proto_library(
    name = "event_proto",
    srcs = ["event.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
        "//src/main/java/com/techcubing/proto/wcif:round_proto",
    ],
)

java_proto_library(
    name = "event_java_proto",
    deps = [":event_proto"],
)

proto_library(
    name = "round_proto",
    srcs = ["round.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
    ],
)

java_proto_library(
    name = "round_java_proto",
    deps = [":round_proto"],
)
