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

maven_jar(
    name = "commons_fileupload_commons_fileupload",
    artifact = "commons-fileupload:commons-fileupload:1.3.3",
    sha1 = "04ff14d809195b711fd6bcc87e6777f886730ca1",
    sha1_src = "34c8608c461e2c832a364ec1a9e70f360f47d9f7",
)

maven_jar(
    name = "commons_io_commons_io",
    artifact = "commons-io:commons-io:2.6",
    sha1 = "815893df5f31da2ece4040fe0a12fd44b577afaf",
    sha1_src = "2566800dc841d9d2c5a0d34d807e45d4107dbbdf",
)

maven_jar(
    name = "org_reflections_reflections",
    artifact = "org.reflections:reflections:0.9.10",
    sha1 = "c7f4ea230aedc757ca1499ebcfb2953c81cf8b67",
    sha1_src = "606b8587dab0bf1776b896003f2a2fa0470bb105",
)

maven_jar(
    name = "org_javassist_javassist",
    artifact = "org.javassist:javassist:3.23.1-GA",
    sha1 = "c072c13dcb7f705471c40bafb1536171df850ab2",
    sha1_src = "cfdfb9f2777db7f7f148faa9c6b74c87b5c735d5",
)

maven_jar(
    name = "com_github_pcj_google_options",
    artifact = "com.github.pcj:google-options:jar:1.0.0",
    sha1 = "85d54fe6771e5ff0d54827b0a3315c3e12fdd0c7",
)

# Install buildifier.

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

# buildifier is written in Go and hence needs rules_go to be built.
# See https://github.com/bazelbuild/rules_go for the up to date setup instructions.
http_archive(
    name = "io_bazel_rules_go",
    sha256 = "c1f52b8789218bb1542ed362c4f7de7052abcf254d865d96fb7ba6d44bc15ee3",
    url = "https://github.com/bazelbuild/rules_go/releases/download/0.12.0/rules_go-0.12.0.tar.gz",
)

http_archive(
    name = "com_github_bazelbuild_buildtools",
    strip_prefix = "buildtools-d9a7a40776070d61821f906a188078de6320f2b8",
    url = "https://github.com/bazelbuild/buildtools/archive/d9a7a40776070d61821f906a188078de6320f2b8.zip",
)

load("@io_bazel_rules_go//go:def.bzl", "go_register_toolchains", "go_rules_dependencies")
load("@com_github_bazelbuild_buildtools//buildifier:deps.bzl", "buildifier_dependencies")

go_rules_dependencies()

go_register_toolchains()

buildifier_dependencies()
