package hxy.dragon

import hxy.dragon.entity.param.UserParam
import hxy.dragon.controller.DepartmentController
import io.javalin.Javalin
import mu.KotlinLogging

private val log = KotlinLogging.logger {}


fun main() {

    val app = Javalin.create { config ->
        config.http.defaultContentType = "text/plain; charset=utf-8" // 解决 ctx#result 返回中文乱码。
    }.start(7000)

    app.get("/") { ctx -> ctx.result("Hello World，你好世界！") }

    app.get("/json") { ctx ->
        // some code
        ctx.json(mapOf(1 to "a", 2 to "中文乱码"))
    }

    app.get("/param") { ctx ->
//     参数处理    https://javalin.io/documentation#context
        var name = ctx.queryParam("name")
        log.info { "参数 name = $name" }
        ctx.json(mapOf(1 to name, 2 to "中文乱码"))
    }

    app.post("/user") { ctx ->
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
        var userParam = ctx.bodyAsClass(UserParam::class.java)

        log.info { userParam }

        ctx.json(mapOf(1 to userParam, 2 to "中文乱码"))
    }

    // 增加
    app.post("/department", DepartmentController::create)
//    查询列表
    app.get("/department", DepartmentController::list)
//    修改
    app.put("/department", DepartmentController::update)
//    删除
    app.delete("/department", DepartmentController::delete)

}