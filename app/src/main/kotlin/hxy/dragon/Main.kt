package hxy.dragon

import com.fasterxml.jackson.annotation.JsonInclude
import hxy.dragon.controller.CustomerController
import hxy.dragon.controller.DepartmentController
import hxy.dragon.controller.IndexController
import hxy.dragon.entity.BaseResponse
import io.ebean.DatabaseFactory
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.delete
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.apibuilder.ApiBuilder.put
import io.javalin.json.JavalinJackson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.FileNotFoundException

private val log = KotlinLogging.logger {}


fun main() {

    val app = Javalin.create { config ->
        config.http.asyncTimeout = 10_000L;
        config.useVirtualThreads = true
        config.http.defaultContentType = "text/plain; charset=utf-8" // 解决 ctx#result 返回中文乱码。
        config.router.ignoreTrailingSlashes = true // treat '/path' and '/path/' as the same path 默认就是支持尾斜杠
        config.router.treatMultipleSlashesAsSingleSlash = true // treat '/path//subpath' and '/path/subpath' as the same path
        config.router.caseInsensitiveRoutes = true; // treat '/PATH' and '/path' as the same path
        config.jsonMapper(JavalinJackson().updateMapper { mapper ->
//             如果是字段值是null，那么就序列化直接忽略
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        })
        config.bundledPlugins.enableDevLogging() // 调试请求日志
        config.bundledPlugins.enableRouteOverview("routes") // 访问 http://localhost:7000/routes

        config.router.apiBuilder {

            get("/") { ctx ->
                log.info { "首页访问中" }
                ctx.result("Hello World，你好世界！—— Javalin6 for java & kotlin")
            }
            get("/param", IndexController::paramReceive)
            post("/user", IndexController::bodyReceive)


            // 组合式 Handler groups https://javalin.io/documentation#handler-groups
            path("customer") {
                // 下面是ebean操作的
                post(CustomerController::createCustomer)
                delete(CustomerController::deleteCustomer)
                put(CustomerController::updateCustomer)
                get(CustomerController::getOne)
                path("list") {
                    get(CustomerController::listCustomer)
                }
            }


            //    增加
            post("/department", DepartmentController::create)
            //    删除
            delete("/department", DepartmentController::delete)
            //    修改
            put("/department", DepartmentController::update)
            //    查询
            get("/department", DepartmentController::getOne)
            //    查询列表
            get("/department/list", DepartmentController::list)
        }
    }.events { event ->
        event.serverStarting {
            // 启动的时候加载配置信息，给数据库加上配置。

            // 读取 JSON 文件内容
            // 读取配置文件，从配置文件中加载这个变量。 【腾讯文档】系统配置信息 https://docs.qq.com/doc/DSFdLamxuUXNWRVZJ
            val databaseJsonFile = System.getProperty("user.home") + "/.config/eric-config/database.json"
            val jsonContent = readJsonFile(databaseJsonFile)

            // 使用 Kotlin 标准库中的 Json 对象解析 JSON 字符串为 JsonObject
            val jsonObject = Json.parseToJsonElement(jsonContent).jsonObject

            // 获取 JSON 中的具体字段值
            val url = jsonObject["spring.datasource.url"]?.jsonPrimitive?.contentOrNull
            val username = jsonObject["spring.datasource.username"]?.jsonPrimitive?.contentOrNull
            val password = jsonObject["spring.datasource.password"]?.jsonPrimitive?.contentOrNull

            // 打印获取到的值
            println("数据库链接: $url")

            // https://ebean.io/docs/intro/configuration/#factory
            // datasource
            val dataSourceConfig = DataSourceConfig()
            dataSourceConfig.setUrl(url)
            dataSourceConfig.setUsername(username)
            dataSourceConfig.setPassword(password)
            dataSourceConfig.setPlatform("mysql")

            // configuration ...
            val config = DatabaseConfig();
            config.setDataSourceConfig(dataSourceConfig)

            // create database instance
            val database = DatabaseFactory.create(config)

            // 自动建表
            val sqlUpdate = database.sqlUpdate(
                """
                    CREATE TABLE if not exists  `customer1`   (
                                                `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                                `email` varchar(120) DEFAULT NULL,
                                                `name` varchar(11) DEFAULT NULL,
                                                PRIMARY KEY (`id`)
                    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
                """.trimIndent()
            )
            val execute = sqlUpdate.execute()
            if (execute > 0) {
                log.info { "第一次自动建表成功 $execute" }
            }
        }
    }.start(7000)

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

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info { "服务器停止了" }
        app.stop()
    })

}

// 读取 JSON 文件的函数
fun readJsonFile(fileName: String): String {
    return try {
        // 读取文件内容
        val content = java.io.File(fileName).readText(Charsets.UTF_8)
        content
    } catch (e: Exception) {
        // 处理异常，比如文件不存在等情况
        log.error { e }
        ""
    }
}
