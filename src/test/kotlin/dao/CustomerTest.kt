package dao;

import hxy.dragon.model.Customer
import io.ebean.DB
import kotlin.test.Test


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
}
  