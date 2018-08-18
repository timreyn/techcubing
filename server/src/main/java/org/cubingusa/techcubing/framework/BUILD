package(
    default_visibility = ["//src/main/java/org/cubingusa/techcubing:__subpackages__"],
)

java_library(
    name = "ServerState",
    srcs = ["ServerState.java"],
    deps = [
        ":MysqlConnection",
        "@org_freemarker_freemarker//jar",
    ],
)

java_library(
    name = "ServerStateInitializer",
    srcs = ["ServerStateInitializer.java"],
    deps = [
        ":MysqlConnection",
        ":ServerState",
        "@org_freemarker_freemarker//jar",
    ],
    visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
)

java_library(
    name = "OAuth",
    srcs = ["OAuth.java"],
    deps = [
        ":ServerState",
        "@javax_javaee_web_api//jar",
    ],
)

java_library(
    name = "QueryParser",
    srcs = ["QueryParser.java"],
)

java_library(
    name = "MysqlConnection",
    srcs = ["MysqlConnection.java"],
    deps = [
        "@com_google_protobuf//:protobuf_java",
        "@mysql_mysql_connector_java//jar",
    ],
)
