package(default_visibility = ["//visibility:public"])

java_library(
    name = "WcifUtil",
    srcs = ["WcifUtil.java"],
    deps = [
        "//src/main/java/com/techcubing/proto/wcif:round_java_proto",
    ],
)

java_library(
    name = "OAuth",
    srcs = ["OAuth.java"],
    deps = [
        "@javax_javaee_web_api//jar",
        "//src/main/java/com/techcubing/server/framework:ServerState",
    ],
)

java_library(
    name = "QueryParser",
    srcs = ["QueryParser.java"],
)

java_library(
    name = "ProtoUtil",
    srcs = ["ProtoUtil.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/proto:options_java_proto",
    ],
)
