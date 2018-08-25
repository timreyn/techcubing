package(default_visibility = [
    "//src/main/java/com/techcubing/proto:__subpackages__",
    "//src/main/java/com/techcubing/server:__subpackages__",
])

# person.proto

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

# competition.proto

proto_library(
    name = "competition_proto",
    srcs = ["competition.proto"],
    deps = [
        "//src/main/java/com/techcubing/proto:options_proto",
        "//src/main/java/com/techcubing/proto/wcif:event_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_proto",
    ],
)
java_proto_library(
    name = "competition_java_proto",
    deps = [":competition_proto"],
)

# event.proto

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

# round.proto

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

# lite proto library for android

java_lite_proto_library(
    name = "lite",
    deps = [
        ":competition_proto",
        ":event_proto",
        ":person_proto",
        ":round_proto",
    ],
    visibility = [
        "//src/main/java/com/techcubing/android:__subpackages__",
    ],
)
