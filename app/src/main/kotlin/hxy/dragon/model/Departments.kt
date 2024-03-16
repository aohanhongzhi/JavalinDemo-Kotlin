package hxy.dragon.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/** 说实话，下面这种方式写法还是太复杂了
 * @author eric
 */
@Deprecated(message = "推荐使用ebean")
// 对Service层的。 这里神奇就是 interface 也能反序列化接收参数和序列化返回数据
interface Department : Entity<Department> {
    companion object : Entity.Factory<Department>()

    val id: Int
    var name: String
    var location: String
}

// 数据库层的
@Deprecated(message = "推荐使用ebean")
object Departments : Table<Department>("t_department") {
    //    注意下面一定要绑定 bindTo 否则没有值
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val location = varchar("location").bindTo { it.location }
}

// 申明这个ORM层的操作句柄
@Deprecated(message = "推荐使用ebean")
val Database.departments get() = this.sequenceOf(Departments)
