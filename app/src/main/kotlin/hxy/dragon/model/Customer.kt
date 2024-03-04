package hxy.dragon.model

import io.ebean.Model
import io.ebean.annotation.Length
import jakarta.persistence.Entity
import jakarta.persistence.Id

typealias  CustomerId = Long // 类型别名提高代码的可读性与可维护性

/**
 *  最好设置一个DAO层，用封装 ebean，否则项目大了不好调试
 */
@Entity
class Customer : Model() {

    @Id
    var id: CustomerId = 0 //默认值为 0

    var name: String? = null // 默认值为null

    @Length(100)
    var email: String? = null
}
  