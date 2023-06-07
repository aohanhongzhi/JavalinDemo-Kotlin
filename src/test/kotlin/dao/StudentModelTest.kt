package dao

import hxy.dragon.entity.enums.Sex
import hxy.dragon.model.StudentModel
import io.ebean.DB
import kotlin.test.Test

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
    }

    @Test
    fun `test student update`() {
        val studentModel = StudentModel(null)
        studentModel.id = 56
        DB.update(studentModel)
    }

}