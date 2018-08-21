android_binary(
    name = "app",
    manifest = "AndroidManifest.xml",
    custom_package = "com.techcubing.android",
    deps = [
        ":activities",
    ],
    resource_files = glob(["res/**"]),
)

android_library(
    name = "activities",
    srcs = glob(["activities/*.java"]),
    manifest = "AndroidManifest.xml",
    custom_package = "com.techcubing.android.activities",
    deps = [
        "@androidsdk//com.android.support:appcompat-v7-25.3.1",
    ],
    resource_files = glob(["res/**"]),
)
