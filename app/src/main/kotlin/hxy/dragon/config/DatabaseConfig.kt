package hxy.dragon.config

import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel

@Deprecated(message = "推荐使用ebean")
val database = Database.connect(
    "jdbc:mysql://mysql.cupb.top:3306/eric",
    user = "eric",
    password = "dream,1234..",
    logger = ConsoleLogger(threshold = LogLevel.DEBUG)
)
