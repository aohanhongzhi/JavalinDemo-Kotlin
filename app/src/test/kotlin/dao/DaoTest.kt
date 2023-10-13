package dao

import com.fasterxml.jackson.databind.ObjectMapper
import hxy.dragon.model.Department
import hxy.dragon.model.Departments
import hxy.dragon.model.departments
import io.github.oshai.kotlinlogging.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import kotlin.test.Test

/**
 * @description
 * @author eric
 * @date 2023/5/29
 */
private val log = KotlinLogging.logger {}


class DaoTest {

    @Test
    fun `database test`() {


        val objectMapper = ObjectMapper()
        objectMapper.findAndRegisterModules()


        val database =
            Database.connect("jdbc:mysql://mysql.cupb.top:3306/eric", user = "eric", password = "dream,1234..")

        database.insert(Departments) {
            set(it.name, "Java")
            set(it.location, "Shanghai")
        }

        val select = database.from(Departments).select()
        for (row in select) {
            println(row[Departments.name])
        }


        database.delete(Departments) { it.id eq 4 }

        val employee = database.departments.find { it.id eq 2 }

//        FIXME 有值，但是属性为空
        log.info("Employ $employee")

        val departments1 = database.departments.toCollection(ArrayList())
        log.info("departments1 $departments1")

        val employees = database.departments
            .filter { it.name eq "Java" }
            .toList()

//        FIXME 有值，但是属性为空，查出list不为0
        log.info("Employs $employees")

        database.departments.add(Department { name = "a"; location = "c" })


    }


}