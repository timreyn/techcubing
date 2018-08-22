android_binary(
    name = "app",
    custom_package = "com.techcubing.android",
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        ":activities",
    ],
)

android_library(
    name = "activities",
    srcs = glob(["activities/*.java"]),
    custom_package = "com.techcubing.android.activities",
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    deps = [
        "//src/main/java/com/techcubing/android:util",
        "//src/main/java/com/techcubing/proto:device_config_java_lite_proto",
        "@androidsdk//com.android.support:appcompat-v7-25.3.1",
        "@com_google_protobuf_javalite//:protobuf_java_lite",
    ],
)

android_library(
    name = "util",
    srcs = glob(["util/*.java"]),
    custom_package = "com.techcubing.android.activities",
)
