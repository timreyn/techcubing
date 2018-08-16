package(
    default_visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
)

java_library(
    name = "ServerState",
    srcs = ["ServerState.java"],
    deps = [
        "@org_freemarker_freemarker//jar",
    ],
)

java_library(
    name = "HomeHandler",
    srcs = ["HomeHandler.java"],
    deps = [
        ":ServerState",
        "@org_freemarker_freemarker//jar",
    ],
)
