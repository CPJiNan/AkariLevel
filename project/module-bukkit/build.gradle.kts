dependencies {
    // 引入 API
    compileOnly(project(":project:common"))
    // 引入 服务端核心
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
}

// 子模块
taboolib { subproject = true }