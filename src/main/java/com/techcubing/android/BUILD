load("@gmaven_rules//:defs.bzl", "gmaven_artifact")

android_binary(
    name = "app",
    custom_package = "com.techcubing.android",
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        ":activities",
    ],
)

genrule(
    name = "camerakit_genrule",
    srcs = ["@camerakit//file"],
    outs = ["camerakit.aar"],
    cmd = "cp $(SRCS) $@",
)

aar_import(
    name = "com_wonderkiln_camerakit",
    aar = ":camerakit.aar",
)

android_library(
    name = "activities",
    srcs = glob(["activities/*.java"]),
    custom_package = "com.techcubing.android.activities",
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        gmaven_artifact("com.android.support:exifinterface:aar:26.1.0"),
        gmaven_artifact("com.google.firebase:firebase-ml-vision:aar:16.0.0"),
        gmaven_artifact("com.google.android.gms:play-services-tasks:aar:15.0.1"),
        "//src/main/java/com/techcubing/android:util",
        "//src/main/java/com/techcubing/proto:device_config_java_lite_proto",
        "//src/main/java/com/techcubing/proto:device_type_java_lite_proto",
        "@androidsdk//com.android.support:appcompat-v7-25.3.1",
        "@com_google_protobuf_javalite//:protobuf_java_lite",
        ":com_wonderkiln_camerakit",
    ],
)

android_library(
    name = "util",
    srcs = glob(["util/*.java"]),
    custom_package = "com.techcubing.android.activities",
)
