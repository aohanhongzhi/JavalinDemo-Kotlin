package hxy.dragon.model

import io.ebean.Model
import javax.persistence.Entity
import javax.persistence.Id

/**
 *  最好设置一个DAO层，用封装 ebean，否则项目大了不好调试
 */
@Entity
class Customer : Model() {

    @Id
    var id: Long = 0

    var name: String? = null
}
  