load("@com_github_bazelbuild_buildtools//buildifier:def.bzl", "buildifier")

buildifier(
    name = "buildifier",
)

filegroup(
    name = "qrcode_js",
    srcs = ["third_party/qrcodejs/qrcode.min.js"],
    visibility = ["//src/main/java/com/techcubing/server:__pkg__"],
)
