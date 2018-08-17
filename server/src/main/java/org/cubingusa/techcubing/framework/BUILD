package(
    default_visibility = ["//src/main/java/org/cubingusa/techcubing:__subpackages__"],
)

java_library(
    name = "ServerState",
    srcs = ["ServerState.java"],
    deps = [
        "@org_freemarker_freemarker//jar",
    ],
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
