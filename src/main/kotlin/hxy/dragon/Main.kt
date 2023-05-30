package hxy.dragon

import com.fasterxml.jackson.annotation.JsonInclude
import hxy.dragon.entity.param.UserParam
import hxy.dragon.controller.DepartmentController
import hxy.dragon.entity.BaseResponse
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import mu.KotlinLogging
import java.io.FileNotFoundException

private val log = KotlinLogging.logger {}


fun main() {

    val app = Javalin.create { config ->
        config.http.defaultContentType = "text/plain; charset=utf-8" // 解决 ctx#result 返回中文乱码。
//        config.routing.ignoreTrailingSlashes = true // treat '/path' and '/path/' as the same path 默认就是支持尾斜杠
        // treat '/path//subpath' and '/path/subpath' as the same path
        config.routing.treatMultipleSlashesAsSingleSlash = true
        config.jsonMapper(JavalinJackson().updateMapper { mapper ->
//             如果是字段值是null，那么就序列化直接忽略
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        })
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
    app.get("/department", DepartmentController::getOne)
//    查询列表
    app.get("/department/list", DepartmentController::list)
//    修改
    app.put("/department", DepartmentController::update)
//    删除
    app.delete("/department", DepartmentController::delete)

//    app.error(404) { ctx ->
//        log.info { "path不存在 : $ctx.req().requestURI" }
//        ctx.json(BaseResponse(404, "路径或者方法不存在", ctx.req().requestURI))
//    }

    app.exception(FileNotFoundException::class.java) { e, ctx ->
        log.info { "path不存在 : $ctx.req().requestURI" }
        ctx.status(404)
    }.error(404) { ctx ->
        val req = ctx.req()
        log.info {
            "方法或者路径不存在，method: ${req.method},path: ${req.requestURI.toString()}"
        }
        ctx.json(BaseResponse(404, "路径或者方法不存在", req.method + ":" + ctx.req().requestURI))
    }

    // HTTP exceptions
    app.exception(NullPointerException::class.java) { e, ctx ->
        // handle nullpointers here
        log.info { e }
    }

    app.exception(Exception::class.java) { e, ctx ->
        // handle general exceptions here
        // will not trigger if more specific exception-mapper found
        log.info { e }
    }

}