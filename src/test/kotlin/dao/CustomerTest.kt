package dao;

import hxy.dragon.model.Customer;
import io.ebean.DB;
import org.junit.jupiter.api.Test;


class CustomerTest  {

  @Test
  fun `insert_update_delete`() {

    val customer = Customer()
    customer.name  = "Hello entity bean"

    // insert
    DB.save(customer)

    // find by Id
    var found = DB.find(Customer::class.java, 1);

    DB.delete(found);
  }
}
  