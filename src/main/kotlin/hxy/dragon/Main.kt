package hxy.dragon

import hxy.dragon.entity.BaseResponse
import hxy.dragon.entity.param.UserParam
import hxy.dragon.model.Department
import hxy.dragon.model.departments
import io.javalin.Javalin
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.toCollection
import org.ktorm.entity.update

private val log = KotlinLogging.logger {}

val database = Database.connect("jdbc:mysql://mysql.cupb.top:3306/eric", user = "eric", password = "dream,1234..")

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
//     参数处理    https://javalin.io/documentation#context
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
        var userParam = ctx.bodyAsClass(UserParam::class.java)

        log.info { userParam }

        ctx.json(mapOf(1 to userParam, 2 to "中文乱码"))
    }
    app.post("/department") { ctx ->
//     参数处理    https://javalin.io/documentation#context
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
//         这里的申请神奇之处，就是interface竟然可以反序列化 kotlinx.serialize是不行的
        var department = ctx.bodyAsClass(Department::class.java)

        log.info { "增加 $department" }

        val add = database.departments.add(department)

        ctx.json(BaseResponse(200, "success", add))

    }

    app.get("/department") { ctx ->

        // 下面数据转换就比较麻烦了，不是很简洁，虽然过程确实是这样。但是无奈不如Java的现有框架简单。

//        val departments = database.from(DepartmentsTable).select().map { row ->
//            // 这一块，要一直hardcode就难受了，如果量特别大
//            DepartmentData(
//                id = row[DepartmentsTable.id] ?: error("Id shouldn't be null"),
//                name = row[DepartmentsTable.name] ?: error("Name shouldn't be null"),
//                location = row[DepartmentsTable.location] ?: error("Age shouldn't be null")
//            )
//        }

        // 更简单方式
        val departments = database.departments.toCollection(ArrayList())

        if (departments.isEmpty()) {
            // 添加
            database.departments.add(Department {
                name = "add";
                location = "address"
            })
        }

        log.info { "数据一共有 ${departments.size} 条数据" }

        ctx.json(BaseResponse(200, "success", departments))
    }

    app.put("/department") { ctx ->
//     参数处理    https://javalin.io/documentation#context
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
        var department = ctx.bodyAsClass(Department::class.java)

        log.info { "修改 $department" }

        val update = database.departments.update(department)

        ctx.json(BaseResponse(200, "success", update))

    }

    app.delete("/department") { ctx ->
//     参数处理    https://javalin.io/documentation#context
//        https://blog.csdn.net/ZHXLXH/article/details/127084395
        var department = ctx.bodyAsClass(Department::class.java)

        log.info { "删除 $department" }

        if (false) {
            val department1 = database.departments.find { it.id eq department.id }
            val delete = department1?.delete()
        }
        // 直接删除
        val removeIf = database.departments.removeIf { it.id eq department.id }

        ctx.json(BaseResponse(200, "success", removeIf))
    }

}