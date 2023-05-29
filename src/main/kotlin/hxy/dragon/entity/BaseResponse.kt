package hxy.dragon.entity

/**
 * @description 加上泛型，而不是 Any
 * @author eric
 * @date 2023/5/29
 */
data class BaseResponse<T>(var code: Int = 200, var message: String, var data: T)
