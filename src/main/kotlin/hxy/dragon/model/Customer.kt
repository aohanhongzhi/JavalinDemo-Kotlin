package hxy.dragon.model

import io.ebean.Model
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Customer : Model() {

    @Id
    var id: Long = 0

    var name: String? = null
}
  