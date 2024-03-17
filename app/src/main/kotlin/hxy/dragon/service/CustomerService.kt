package hxy.dragon.service

import hxy.dragon.entity.BaseResponse
import hxy.dragon.model.Customer
import io.ebean.DB
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

// 代码定义了一个名为 CustomerService 的对象（object）。在 Kotlin 中，对象（object）可以看作是一个单例对象，具有静态方法的特性。这意味着你可以直接通过 CustomerService.list() 的方式调用该对象的 list 方法。
object CustomerService {

    fun save() {

    }

    fun delete(id: Int): BaseResponse<String> {
        val delete = DB.find(Customer::class.java).where().eq("id", id).delete()
        return BaseResponse(200, "删除结果", "删除影响行数：" + delete)
    }

    /**
     * ebean 查询list
     */
    fun list(): List<Customer> {
        val findList = DB.find(Customer::class.java).findList()
        return findList
    }

    fun getOne(id: Int): BaseResponse<Customer> {
        val customer = DB.find(Customer::class.java).where().eq("id", id).findOne()
        var obj = BaseResponse(200, "查询到了", customer)
        if (customer == null) {
            obj = BaseResponse(404, "没有找到数据", customer)
        }
        return obj
    }

    fun updateCustomer(customer: Customer): BaseResponse<Customer> {
        // 更新方式1
        customer.update()

        if (false) {
            // 更新方式2
            DB.update(customer)

            // 更新方式3：更新指定字段
            val rows = DB.update(Customer::class.java).set("name", customer.name).where().eq("id", customer.id).update()
            log.info { "更新影响行数 $rows" }
        }
        return BaseResponse(200, "更新结果", customer)
    }
}