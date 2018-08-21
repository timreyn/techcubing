package(
    default_visibility = ["//src/main/java/com/techcubing/server:__subpackages__"],
)

java_library(
    name = "ServerState",
    srcs = ["ServerState.java"],
    deps = [
        ":MysqlConnection",
        ":ProtoRegistry",
        "@com_android_tools_ddms_ddmlib//jar",
        "@org_freemarker_freemarker//jar",
    ],
)

java_library(
    name = "ServerStateInitializer",
    srcs = ["ServerStateInitializer.java"],
    deps = [
        ":MysqlConnection",
        ":ProtoRegistry",
        ":ServerState",
        "@com_android_tools_ddms_ddmlib//jar",
        "@org_freemarker_freemarker//jar",
        "//src/main/java/com/techcubing/proto:device_java_proto",
        "//src/main/java/com/techcubing/proto:scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:competition_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:event_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:round_java_proto",
    ],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
)

java_library(
    name = "MysqlConnection",
    srcs = ["MysqlConnection.java"],
    deps = [
        "@mysql_mysql_connector_java//jar",
    ],
)

java_library(
    name = "ProtoRegistry",
    srcs = ["ProtoRegistry.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "ProtoDb",
    srcs = ["ProtoDb.java"],
    deps = [
        ":MysqlConnection",
        ":ServerState",
        "@com_google_protobuf//:protobuf_java",
        "//src/main/java/com/techcubing/proto:options_java_proto",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
    ],
)

java_library(
    name = "ScorecardGenerator",
    srcs = ["ScorecardGenerator.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        ":ProtoDb",
        ":ServerState",
        "//src/main/java/com/techcubing/proto:scorecard_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:event_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:person_java_proto",
        "//src/main/java/com/techcubing/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
    ],
)
