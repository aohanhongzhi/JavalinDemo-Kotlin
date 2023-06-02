package hxy.dragon.controller

import hxy.dragon.entity.param.UserParam
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.http.Context

/**
 * @description
 * @author eric
 * @date 2023/5/30
 */
private val log = KotlinLogging.logger {}


object IndexController {

    fun paramReceive(ctx: Context) {
        //     参数处理    https://javalin.io/documentation#context
        var name = ctx.queryParam("name")
        log.info { "参数 name = $name" }

//        创建ArrayList
        var arrayList = arrayListOf<String>()
        if (name != null) {
            arrayList.add(name)
        }
//        创建Map
        ctx.json(mapOf(1 to name, 2 to "中文乱码"))
    }

    fun bodyReceive(ctx: Context) {
        // https://blog.csdn.net/ZHXLXH/article/details/127084395
        var userParam = ctx.bodyAsClass(UserParam::class.java)

        log.info { "反序列化后的参数 $userParam" }

        ctx.json(mapOf(1 to userParam, 2 to "中文乱码"))
    }

}