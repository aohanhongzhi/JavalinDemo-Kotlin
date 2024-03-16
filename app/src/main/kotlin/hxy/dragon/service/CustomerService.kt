package hxy.dragon.service

import hxy.dragon.model.Customer
import io.ebean.DB

// 代码定义了一个名为 CustomerService 的对象（object）。在 Kotlin 中，对象（object）可以看作是一个单例对象，具有静态方法的特性。这意味着你可以直接通过 CustomerService.list() 的方式调用该对象的 list 方法。
object CustomerService {

    /**
     * ebean 查询list
     */
    fun list(): List<Customer> {
        val findList = DB.find(Customer::class.java).findList()
        return findList
    }
}