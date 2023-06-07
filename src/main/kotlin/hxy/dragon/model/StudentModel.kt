package hxy.dragon.model

import hxy.dragon.entity.enums.Sex
import io.ebean.Model
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

/**
 * @description
 * @author eric
 * @date 2023/6/7
 */
@Entity
class StudentModel(name: String?) : Model() {
    @Id
    var id: Long? = null // 默认值为null

    @Column(length = 150, unique = true)
    var name: String? = name // 从上面传过来设置，也就是构造器必须填参数

    @Enumerated(EnumType.STRING)
    var sex: Sex = Sex.DEFAULT //默认参数
}