java_binary(
    name = "main",
    srcs = ["Main.java"],
    main_class = "org.cubingusa.techcubing.Main",
    deps = [
        "@org_freemarker_freemarker//jar",
        "//src/main/java/org/cubingusa/techcubing/framework:ServerState",
        "//src/main/java/org/cubingusa/techcubing/framework:MysqlConnection",
        "//src/main/java/org/cubingusa/techcubing/handlers:CompetitionsHandler",
        "//src/main/java/org/cubingusa/techcubing/handlers:HomeHandler",
        "//src/main/java/org/cubingusa/techcubing/handlers:OAuthRedirectHandler",
    ],
    runtime_deps = [
        "@org_glassfish_jersey_core_jersey_common//jar",
    ],
)
