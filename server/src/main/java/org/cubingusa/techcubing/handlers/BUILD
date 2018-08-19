package(
    default_visibility = ["//src/main/java/org/cubingusa/techcubing:__pkg__"],
)

java_library(
    name = "BaseHandler",
    srcs = ["BaseHandler.java"],
    deps = [
        "@org_freemarker_freemarker//jar",
        "@com_googlecode_json_simple_json_simple//jar",
        "@javax_javaee_web_api//jar",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/util:OAuth",
        "//src/main/java/org/cubingusa/techcubing/util:QueryParser",
    ],
)

java_library(
    name = "IndexHandler",
    srcs = ["IndexHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
    ],
)

java_library(
    name = "OAuthRedirectHandler",
    srcs = ["OAuthRedirectHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/util:QueryParser",
    ],
)

java_library(
    name = "CompetitionsHandler",
    srcs = ["CompetitionsHandler.java"],
    deps = [
        ":BaseHandler",
        "@com_googlecode_json_simple_json_simple//jar",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
    ],
)

java_library(
    name = "SetCompetitionHandler",
    srcs = ["SetCompetitionHandler.java"],
    deps = [
        ":BaseHandler",
        "@com_google_protobuf//:protobuf_java_util",
        "@com_googlecode_json_simple_json_simple//jar",
        "//src/main/java/org/cubingusa/techcubing/framework:ProtoDb",
        "//src/main/java/org/cubingusa/techcubing/framework:ScorecardGenerator",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/proto/wcif:competition_java_proto",
    ],
)
