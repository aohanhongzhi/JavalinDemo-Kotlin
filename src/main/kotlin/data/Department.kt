package data

import org.ktorm.entity.Entity

interface Department : Entity<Department> {
    val id: Int
    var name: String
    var location: String
}