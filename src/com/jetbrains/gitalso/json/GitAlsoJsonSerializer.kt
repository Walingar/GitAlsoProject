package com.jetbrains.gitalso.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.math.BigDecimal

object GitAlsoJsonSerializer {

    // Gson which prints double numbers with 2 digits after .
    private val gson = GsonBuilder().serializeNulls().registerTypeAdapter(object: TypeToken<Double>() {}.type, JsonSerializer<Double>
    { value, _, _ -> JsonPrimitive(BigDecimal(value.toDouble()).setScale(2, BigDecimal.ROUND_HALF_UP)) }).create()

    fun toJson(obj: Any): String = gson.toJson(obj)

    fun <T> fromJsonToType(json: String, typeOfT: Type): T {
        return gson.fromJson(json, typeOfT)
    }

    fun <T> fromJsonToClass(json: String, obj: Class<T>): T {
        return gson.fromJson(json, obj)
    }
}