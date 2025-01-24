dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    // 引入 服务端核心
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    // 引入 依赖
    compileOnly("org.openjdk.nashorn:nashorn-core:15.6")
}

// 子模块
taboolib { subproject = true }