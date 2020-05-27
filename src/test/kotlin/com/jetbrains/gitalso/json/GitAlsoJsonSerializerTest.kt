package com.jetbrains.gitalso.json

import com.google.gson.reflect.TypeToken
import org.junit.Test

class GitAlsoJsonSerializerTest {
    private data class TestClass(val Success: Boolean, val Message: String, val s: List<Int>)

    @Test
    fun fromJsonToClass() {
        val json = "{\"Success\":true,\"Message\":\"Invalid access token.\", 's':[1, 2]}"
        val tmp = GitAlsoJsonSerializer.fromJsonToClass(json, TestClass::class.java)
        print(tmp)
    }

    @Test
    fun fromJsonToType() {
        val json = "{\"Success\":true,\"Message\":\"Invalid access token.\", 's':[1, 2]}"
        val tmp = GitAlsoJsonSerializer.fromJsonToType<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
        print(tmp)
    }

}