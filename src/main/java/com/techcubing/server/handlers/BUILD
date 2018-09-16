package(
    default_visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
)

java_library(
    name = "handlers",
    srcs = glob(["*.java"]),
    deps = [
        "//src/main/proto:device_config_java_proto",
        "//src/main/proto:device_java_proto",
        "//src/main/proto:device_type_java_proto",
        "//src/main/proto:scorecard_java_proto",
        "//src/main/proto:scramble_java_proto",
        "//src/main/proto/wcif:competition_java_proto",
        "//src/main/proto/wcif:event_java_proto",
        "//src/main/proto/wcif:person_java_proto",
        "//src/main/proto/wcif:round_java_proto",
        "//src/main/java/com/techcubing/server/framework",
        "//src/main/java/com/techcubing/server/util:OAuth",
        "//src/main/java/com/techcubing/server/util:ProtoUtil",
        "//src/main/java/com/techcubing/server/util:QueryParser",
        "//src/main/java/com/techcubing/server/util:WcifUtil",
        "@com_android_tools_ddms_ddmlib//jar",
        "@com_googlecode_json_simple_json_simple//jar",
        "@com_google_protobuf//:protobuf_java",
        "@com_google_protobuf//:protobuf_java_util",
        "@commons_fileupload_commons_fileupload//jar",
        "@commons_io_commons_io//jar",
        "@javax_javaee_web_api//jar",
        "@org_freemarker_freemarker//jar",
        "@org_reflections_reflections//jar",
    ],
)
