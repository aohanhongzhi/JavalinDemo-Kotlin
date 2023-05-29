package dao

import model.DepartmentsTable
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import kotlin.test.Test

/**
 * @description
 * @author eric
 * @date 2023/5/29
 */
private val log = KotlinLogging.logger {}

val Database.departments get() = this.sequenceOf(DepartmentsTable)

class DaoTest {

    @Test
    fun `database test`() {
        val database =
            Database.connect("jdbc:mysql://mysql.cupb.top:3306/eric", user = "eric", password = "dream,1234..")

        database.insert(DepartmentsTable) {
            set(it.name, "Java")
            set(it.location, "Shanghai")
        }

        val select = database.from(DepartmentsTable).select()
        for (row in select) {
            println(row[DepartmentsTable.name])
        }


        database.delete(DepartmentsTable) { it.id eq 4 }

        val employee = database.departments.find { it.id eq 2 }

//        FIXME 有值，但是属性为空
        log.info("Employ $employee")

        val employees = database.departments
            .filter { it.name eq "Java" }
            .toList()

//        FIXME 有值，但是属性为空，查出list不为0
        log.info("Employs $employees")

    }


}