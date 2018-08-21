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
        "@androidsdk//com.android.support:appcompat-v7-25.3.1",
    ],
)
