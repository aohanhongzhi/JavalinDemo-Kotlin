package hxy.dragon

import com.fasterxml.jackson.annotation.JsonInclude
import hxy.dragon.controller.CustomerController
import hxy.dragon.controller.DepartmentController
import hxy.dragon.controller.IndexController
import hxy.dragon.entity.BaseResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.json.JavalinJackson
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
        config.plugins.enableDevLogging() // 调试请求日志
        config.plugins.enableRouteOverview("routes") // 访问 http://localhost:7000/routes
    }.start(7000)

    routes(app)

//    app.error(404) { ctx ->
//        log.info { "path不存在 : $ctx.req().requestURI" }
//        ctx.json(BaseResponse(404, "路径或者方法不存在", ctx.req().requestURI))
//    }

    app.exception(FileNotFoundException::class.java) { e, ctx ->
        log.warn { "path不存在 : $ctx.req().requestURI $e" }
        ctx.status(404)
    }.error(404) { ctx ->
        val req = ctx.req()
        log.warn {
            "方法或者路径不存在，method: ${req.method},path: ${req.requestURI}"
        }
        ctx.json(BaseResponse(404, "路径或者方法不存在", req.method + ":" + ctx.req().requestURI))
    }

    // HTTP exceptions
    app.exception(NullPointerException::class.java) { e, ctx ->
        // handle nullpointers here
        log.error { "$ctx.req().requestURI 发生NPE $e" }
    }

    app.exception(Exception::class.java) { e, ctx ->
        // handle general exceptions here
        // will not trigger if more specific exception-mapper found
        log.error { "$ctx.req().requestURI 发生异常 $e" }

    }

    Runtime.getRuntime().addShutdownHook(Thread { app.stop() })

}

/**
 * 路由，函数式注册
 */

fun routes(app: Javalin) {
    app.get("/") { ctx ->
        log.info { "首页访问中" }
        ctx.result("Hello World，你好世界！—— Javalin for kotlin")
    }
    app.get("/param", IndexController::paramReceive)
    app.post("/user", IndexController::bodyReceive)


    // 组合式 Handler groups https://javalin.io/documentation#handler-groups
    app.routes() {
        path("customer") {
            post(CustomerController::createCustomer)
            delete(CustomerController::deleteCustomer)
            put(CustomerController::updateCustomer)
            get(CustomerController::getOne)
            path("list") {
                get(CustomerController::listCustomer)
            }
        }
    }

    //    增加
    app.post("/department", DepartmentController::create)
    //    删除
    app.delete("/department", DepartmentController::delete)
    //    修改
    app.put("/department", DepartmentController::update)
    //    查询
    app.get("/department", DepartmentController::getOne)
    //    查询列表
    app.get("/department/list", DepartmentController::list)


}

