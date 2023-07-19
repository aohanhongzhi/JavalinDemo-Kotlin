package base

import hxy.dragon.model.Customer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

private val log = KotlinLogging.logger {}

class BaseTest {

    @Test
    fun `test if`() {
        val customer = Customer()
        customer.id = 8
        customer.name = "Customer"
        customer.takeIf {
            customer.id > 5
        }?.let {
            log.info("$customer")
        }
    }

}