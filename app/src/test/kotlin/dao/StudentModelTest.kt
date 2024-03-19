package dao

import hxy.dragon.entity.enums.Sex
import hxy.dragon.model.StudentModel
import io.ebean.DB
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.test.Test

private val log = KotlinLogging.logger {}

/**
 * @description
 * @author eric
 * @date 2023/6/7
 */

class StudentModelTest {

    @Test
    fun `test student`() {
        val studentModel = StudentModel("eric")
        studentModel.sex = Sex.MALE
        studentModel.save()
//        DB.save(studentModel)
        log.info { "Student: $studentModel" }
    }

    @Test
    fun `test student update`() {
        val studentModel = StudentModel(null)
        studentModel.id = 56
        DB.update(studentModel)
    }

}