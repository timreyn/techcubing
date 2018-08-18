java_binary(
    name = "main",
    srcs = ["Main.java"],
    main_class = "org.cubingusa.techcubing.Main",
    deps = [
        "@org_freemarker_freemarker//jar",
        "@io_grpc_grpc_java//core",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/framework:MysqlConnection",
        "//src/main/java/org/cubingusa/techcubing/handlers:CompetitionsHandler",
        "//src/main/java/org/cubingusa/techcubing/handlers:HomeHandler",
        "//src/main/java/org/cubingusa/techcubing/handlers:OAuthRedirectHandler",
        "//src/main/java/org/cubingusa/techcubing/handlers:SetCompetitionHandler",
        "//src/main/java/org/cubingusa/techcubing/services:TechCubingServiceImpl",
    ],
    runtime_deps = [
        "@io_grpc_grpc_java//netty",
        "@org_glassfish_jersey_core_jersey_common//jar",
    ],
)
