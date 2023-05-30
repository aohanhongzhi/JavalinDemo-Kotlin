package hxy.dragon.controller


import hxy.dragon.config.database
import hxy.dragon.entity.BaseResponse
import hxy.dragon.model.Department
import hxy.dragon.model.departments
import io.javalin.http.Context
import io.javalin.http.queryParamAsClass
import mu.KotlinLogging
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.toCollection
import org.ktorm.entity.update
import java.util.*
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}

/**
 * object 单例，伴生对象，内部类
 * https://blog.csdn.net/sz_chrome/article/details/80740782
 */
object DepartmentController {


    fun getOne(ctx: Context) {
        val id = ctx.queryParamAsClass<Int>("id").get()
//        val id = ctx.queryParamAsClass("id", Int::class.java).get()

        var department = database.departments.find { it.id eq id }

        if (department == null) {
            ctx.json(BaseResponse(404, "数据库没有检索到id=${id}", null))
        } else {
            ctx.json(BaseResponse(200, "查询完成", department))
        }
    }

    fun list(ctx: Context) {
        val timeCost = measureTimeMillis {
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
            var msg = "数据库一共有 ${departments.size} 条数据"
            log.info { msg }

            ctx.json(BaseResponse(200, msg, departments))
        }

        log.info { "耗时 $timeCost ms" }

    }

    fun create(ctx: Context) {
        //         这里的申请神奇之处，就是interface竟然可以反序列化 kotlinx.serialize是不行的
        var department = ctx.bodyAsClass(Department::class.java)

        log.info { "增加 $department" }

        val add = database.departments.add(department)
        ctx.status(201)
        ctx.json(BaseResponse(201, "success", "affect row = $add"))
    }

    /**
     * 修改
     */
    fun update(ctx: Context) {
        var department = ctx.bodyAsClass(Department::class.java)

        log.info { "修改 $department" }

        val update = database.departments.update(department)

        ctx.json(BaseResponse(200, "success", update))
    }

    fun delete(ctx: Context) {
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

    private fun randomId() = UUID.randomUUID().toString()

}