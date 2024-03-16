package hxy.dragon.service

import hxy.dragon.model.Customer
import io.ebean.DB

// object 的使用方式很像静态方法，但是实际上应该是直接生成了一个对象，所以可以直接调用里面方法。
object CustomerService {

    /**
     * ebean 查询list
     */
    fun list(): List<Customer> {
        val findList = DB.find(Customer::class.java).findList()
        return findList
    }
}