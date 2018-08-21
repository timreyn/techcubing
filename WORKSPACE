local_repository(
    name = "io_grpc_grpc_java",
    path = "third_party/grpc-java",
)

local_repository(
    name = "bazel_skylib",
    path = "third_party/bazel-skylib",
)

load("@io_grpc_grpc_java//:repositories.bzl", "grpc_java_repositories")
grpc_java_repositories()

maven_jar(
    name = "io_grpc_grpc_services",
    artifact = "io.grpc:grpc-services:1.14.0",
    sha1 = "ea4b522eb829a30e43a5695f8590993a70bb92c5",
    sha1_src = "86c9866fb466efe90d633ea25096f0408845fa49",
)

maven_jar(
    name = "org_freemarker_freemarker",
    artifact = "org.freemarker:freemarker:2.3.28",
    sha1 = "7200064467a935052f99d114c2c05c3d189bc6d6",
    sha1_src = "b9e763fd0149515ec3b19cc7025385e7a177bdb0",
)

maven_jar(
    name = "com_googlecode_json_simple_json_simple",
    artifact = "com.googlecode.json-simple:json-simple:1.1.1",
    sha1 = "c9ad4a0850ab676c5c64461a05ca524cdfff59f1",
    sha1_src = "15bba08e3a239d54b68209c001f9c911559d2fed",
)

maven_jar(
    name = "javax_javaee_web_api",
    artifact = "javax:javaee-web-api:8.0",
    sha1 = "dd21587dd7515f8ddad285cf019a6acb18d6e3ba",
    sha1_src = "c0c9f8a15ba50eb7ec73dded40ee46c5d6ce67e6",
)

maven_jar(
    name = "org_glassfish_jersey_core_jersey_common",
    artifact = "org.glassfish.jersey.core:jersey-common:2.27",
    sha1 = "f20d4304a476a92001930d75747adcc232dbe779",
    sha1_src = "ee1a774b4e839b293d7d4ce5bcf59ec70224569c",
)

maven_jar(
    name = "mysql_mysql_connector_java",
    artifact = "mysql:mysql-connector-java:8.0.12",
    sha1 = "08e201602cc1ddd145c4c74e67d4002d3d4b1796",
    sha1_src = "b1eadbd4489464ace2249c118a8d83539f2409f5",
)

maven_jar(
    name = "com_android_tools_ddms_ddmlib",
    artifact = "com.android.tools.ddms:ddmlib:25.3.0",
    sha1 = "8768d9b5888157737306935d477092e12bc6e57a",
    sha1_src = "d1d131be60104381cb5d8f6715c7e3e032185f95",
)

# Android rules

android_sdk_repository(
    name = "androidsdk",
    api_level = 27,
    build_tools_version = '27.0.3',
)
