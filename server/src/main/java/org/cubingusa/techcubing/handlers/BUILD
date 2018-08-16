package(
    default_visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
)

java_library(
    name = "BaseHandler",
    srcs = ["BaseHandler.java"],
    deps = [
        "@org_freemarker_freemarker//jar",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
    ],
)

java_library(
    name = "HomeHandler",
    srcs = ["HomeHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
    ],
)
