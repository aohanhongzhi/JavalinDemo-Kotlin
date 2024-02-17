package hxy.dragon.model

import hxy.dragon.entity.enums.Sex
import io.ebean.Model
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id

/**
 * @description
 * @author eric
 * @date 2023/6/7
 */
@Entity
class StudentModel(name: String?) : Model() {
    /**
     * 加上了 @Id的注解，看其上面备注是 The mapped column for the primary key of the entity is assumed to be the primary key of the primary table
     * 所以对应的数据库应该是需要有主键的
     * 原因是 ebean的 save方法是会去获取返回信息，获取自增的id值
     */
    @Id
    var id: Long? = null // 默认值为null

    @Column(length = 150, unique = true)
    var name: String? = name // 从上面传过来设置，也就是构造器必须填参数

    @Enumerated(EnumType.STRING)
    var sex: Sex = Sex.DEFAULT //默认参数
}