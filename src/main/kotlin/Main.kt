import data.Department
import data.Departments
import entity.param.UserParam
import io.javalin.Javalin
import model.DepartmentsTable
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select

private val log = KotlinLogging.logger {}

val database =
    Database.connect("jdbc:mysql://mysql.cupb.top:3306/eric", user = "eric", password = "dream,1234..")

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
        log.info { name }
        ctx.json(mapOf(1 to name, 2 to "中文乱码"))
    }

    app.post("/user") { ctx ->
//     参数处理    https://javalin.io/documentation#context
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
        var userParam = ctx.bodyAsClass(UserParam::class.java)

        log.info { userParam }

        ctx.json(mapOf(1 to userParam, 2 to "中文乱码"))
    }

    app.get("/department") { ctx ->

        // 下面数据转换就比较麻烦了，不是很简洁，虽然过程确实是这样。但是无奈不如Java的现有框架简单。

        val departments = database.from(DepartmentsTable).select().map { row ->
            // 这一块，要一直hardcode就难受了，如果量特别大
            Departments(
                id = row[DepartmentsTable.id] ?: error("Id shouldn't be null"),
                name = row[DepartmentsTable.name] ?: error("Name shouldn't be null"),
                location = row[DepartmentsTable.location] ?: error("Age shouldn't be null")
            )
        }

        ctx.json(departments)
    }

}