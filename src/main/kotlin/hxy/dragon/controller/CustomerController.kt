package hxy.dragon.controller

import hxy.dragon.entity.BaseResponse
import hxy.dragon.model.Customer
import io.ebean.DB
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context
import io.javalin.http.queryParamAsClass

private val log = KotlinLogging.logger {}

/**
 * @description
 * @author eric
 * @date 2023/5/31
 */
object CustomerController {

    fun createCustomer(ctx: Context) {
        val customer = ctx.bodyAsClass(Customer::class.java)
        customer.save()
        ctx.json(BaseResponse(200, "创建完成", null))
    }

    fun listCustomer(ctx: Context) {
        val findList = DB.find(Customer::class.java).findList()
        ctx.json(BaseResponse(200, "查询list", findList))
    }

    fun getOne(ctx: Context) {
        var id = ctx.queryParamAsClass<Int>("id").get()
        val customer = DB.find(Customer::class.java).where().eq("id", id).findOne()
        ctx.json(BaseResponse(200, "查询结果", customer))
    }

    fun deleteCustomer(ctx: Context) {
        var id = ctx.queryParamAsClass<Int>("id").get()
        val delete = DB.find(Customer::class.java).where().eq("id", id).delete()
        ctx.json(BaseResponse(200, "删除结果", "删除影响行数：" + delete))
    }

    fun updateCustomer(ctx: Context) {
        val customer = ctx.bodyAsClass(Customer::class.java)

        // 更新方式1
        customer.update()

        if (false) {
            // 更新方式2
            DB.update(customer)

            // 更新方式3
            val rows = DB.update(Customer::class.java).set("name", customer.name).where().eq("id", customer.id).update()
            log.info { "更新影响行数 $rows" }
        }

        ctx.json(BaseResponse(200, "更新结果", customer))
    }

}