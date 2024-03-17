package hxy.dragon.controller

import hxy.dragon.entity.BaseResponse
import hxy.dragon.model.Customer
import hxy.dragon.service.CustomerService
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
        var findList = CustomerService.list();
        ctx.json(BaseResponse(200, "查询list", findList))
    }

    fun getOne(ctx: Context) {
        var id = ctx.queryParamAsClass<Int>("id").get()
        var obj = CustomerService.getOne(id)
        ctx.json(obj)
    }

    fun deleteCustomer(ctx: Context) {
        var id = ctx.queryParamAsClass<Int>("id").get()
        ctx.json(CustomerService.delete(id))
    }

    fun updateCustomer(ctx: Context) {
        val customer = ctx.bodyAsClass(Customer::class.java)
        var obj = CustomerService.updateCustomer(customer)
        ctx.json(obj)
    }

}