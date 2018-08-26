package(
    default_visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
)

java_library(
    name = "BaseHandler",
    srcs = ["BaseHandler.java"],
    deps = [
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:OAuth",
        "//src/main/java/com/techcubing/server/util:QueryParser",
        "@com_googlecode_json_simple_json_simple//jar",
        "@javax_javaee_web_api//jar",
        "@org_freemarker_freemarker//jar",
    ],
)

java_library(
    name = "IndexHandler",
    srcs = ["IndexHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/com/techcubing/server/framework:ServerState",
    ],
)

java_library(
    name = "OAuthRedirectHandler",
    srcs = ["OAuthRedirectHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/com/techcubing/server/framework:ServerState",
    ],
)

java_library(
    name = "CompetitionsHandler",
    srcs = ["CompetitionsHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@com_googlecode_json_simple_json_simple//jar",
    ],
)

java_library(
    name = "SetCompetitionHandler",
    srcs = ["SetCompetitionHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/proto/wcif:competition_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ScorecardGenerator",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@com_google_protobuf//:protobuf_java_util",
        "@com_googlecode_json_simple_json_simple//jar",
    ],
)

java_library(
    name = "ManageDevicesHandler",
    srcs = ["ManageDevicesHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "@com_android_tools_ddms_ddmlib//jar",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "AddDeviceHandler",
    srcs = ["AddDeviceHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/proto:device_config_java_proto",
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "@com_android_tools_ddms_ddmlib//jar",
        "@com_google_protobuf//:protobuf_java",
    ],
)

java_library(
    name = "DeleteDeviceHandler",
    srcs = ["DeleteDeviceHandler.java"],
    deps = [
        ":BaseHandler",
        "//src/main/proto:device_java_proto",
        "//src/main/java/com/techcubing/server/framework:ProtoDb",
        "//src/main/java/com/techcubing/server/framework:ServerState",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "@com_google_protobuf//:protobuf_java",
    ],
)
