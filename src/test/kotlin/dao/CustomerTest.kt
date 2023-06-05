package dao;

import hxy.dragon.model.Customer
import io.ebean.DB
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test

private val log = KotlinLogging.logger {}

class CustomerTest {

    @Test
    fun `insert_update_delete`() {

        val id = 1025L
        val customer = Customer()
        customer.name = "Hello entity bean"
        customer.id = id

        // insert
        DB.save(customer)

        // find by Id
        var found = DB.find(Customer::class.java, id)

        // update
        DB.update(Customer::class.java).set("name", "eric").where().eq("id", customer.id).update()

        // find by Id
        var found2 = DB.find(Customer::class.java, id)

        if (found2 != null) {
            DB.delete(found2);
        }
    }

    @Test
    fun `test update null`() {
        val customer = Customer()
        customer.id = 1024
        customer.email = "HelloEntity@qq.com"

//        TODO 需求，不更新null到数据库。 目前是会将null更新到数据库。
        val rows = DB.update(Customer::class.java).set("name", customer.name).set("email", customer.email).where()
            .eq("id", customer.id).update()

        log.info("Customer affect row = {}", rows)

    }
}
  