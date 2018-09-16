package(
    default_visibility = ["//src/main/java/com/techcubing/server:__subpackages__"],
)

java_library(
    name = "framework",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/proto:device_java_proto",
        "//src/main/proto:request_context_java_proto",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto:scramble_java_proto",
        "//src/main/proto:options_java_proto",
        "//src/main/proto:wca_environment_java_proto",
        "//src/main/proto/wcif:competition_java_proto",
        "//src/main/proto/wcif:event_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
        "@com_android_tools_ddms_ddmlib//jar",
        "@com_github_pcj_google_options//jar",
        "@com_google_protobuf//:protobuf_java",
        "@io_grpc_grpc_java//core",
        "@org_freemarker_freemarker//jar",
        "@mysql_mysql_connector_java//jar",
    ],
)
