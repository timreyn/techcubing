package(
    default_visibility = ["//src/main/java/com/techcubing/server:__subpackages__"],
)

java_library(
    name = "ServerState",
    srcs = ["ServerState.java"],
    deps = [
        ":MysqlConnection",
        ":ProtoRegistry",
        "//src/main/proto:wca_environment_java_proto",
        "@com_android_tools_ddms_ddmlib//jar",
        "@org_freemarker_freemarker//jar",
    ],
)

java_library(
    name = "ServerStateInitializer",
    srcs = ["ServerStateInitializer.java"],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
    deps = [
        ":CommandLineFlags",
        ":MysqlConnection",
        ":ProtoRegistry",
        ":ServerState",
        "//src/main/proto:device_java_proto",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto:scramble_java_proto",
        "//src/main/proto/wcif:competition_java_proto",
        "//src/main/proto/wcif:event_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "@com_android_tools_ddms_ddmlib//jar",
        "@org_freemarker_freemarker//jar",
    ],
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
        "//src/main/proto:options_java_proto",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "ScorecardGenerator",
    srcs = ["ScorecardGenerator.java"],
    deps = [
        ":ProtoDb",
        ":ServerState",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto/wcif:event_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "CommandLineFlags",
    srcs = ["CommandLineFlags.java"],
    deps = [
        "//src/main/proto:wca_environment_java_proto",
        ":ServerState",
        "@com_github_pcj_google_options//jar",
    ],
)

java_library(
    name = "TechCubingInterceptor",
    srcs = ["TechCubingInterceptor.java"],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
    deps = [
        ":ProtoDb",
        ":ServerState",
        "//src/main/proto:device_java_proto",
        "//src/main/proto:request_context_java_proto",
        "@com_google_protobuf//:protobuf_java",
        "@io_grpc_grpc_java//core",
    ],
)
