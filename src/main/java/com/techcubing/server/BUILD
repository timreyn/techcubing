java_binary(
    name = "main",
    srcs = ["Main.java"],
    main_class = "com.techcubing.server.Main",
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
        "@org_javassist_javassist//jar",
        "@org_glassfish_jersey_core_jersey_common//jar",
    ],
    deps = [
        "//src/main/java/com/techcubing/server/framework",
        "//src/main/java/com/techcubing/server/handlers",
        "//src/main/java/com/techcubing/server/services",
        "@com_github_pcj_google_options//jar",
        "@io_grpc_grpc_java//core",
        "@io_grpc_grpc_java//protobuf",
        "@io_grpc_grpc_services//jar",
    ],
    resources = [
        "//src/main/java/com/techcubing/server/templates",
        "//:qrcode_js",
    ],
)
