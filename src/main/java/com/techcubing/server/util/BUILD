package(default_visibility = ["//visibility:public"])

java_library(
    name = "WcifUtil",
    srcs = ["WcifUtil.java"],
    deps = [
        "//src/main/proto/wcif:round_java_proto",
    ],
)

java_library(
    name = "OAuth",
    srcs = ["OAuth.java"],
    deps = [
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@javax_javaee_web_api//jar",
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
        "//src/main/proto:options_java_proto",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "Puzzle",
    srcs = [
        "ClockPuzzle.java",
        "CubePuzzle.java",
        "MegaminxPuzzle.java",
        "Puzzle.java",
        "PyraminxPuzzle.java",
        "SkewbPuzzle.java",
        "Sq1Puzzle.java",
    ],
)
