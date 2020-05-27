package com.jetbrains.gitalso.storage.log.hash

import java.security.SecureRandom
import java.util.*

object SaltProvider {

    private val saltFile by lazy {
        SaltFilePathProvider().getSaltFile()
    }

    private fun getSalt(): ByteArray {
        val saltFromFile = saltFile.readText()
        return if (saltFromFile.isBlank()) {
            val random = SecureRandom()
            val bytes = ByteArray(20)
            random.nextBytes(bytes)
            saltFile.writeText(String(bytes))
            bytes
        } else {
            saltFromFile.toByteArray()
        }
    }

    private fun mergeByteArrays(array1: ByteArray, array2: ByteArray): ByteArray {
        val joinedArray = Arrays.copyOf(array1, array1.size + array2.size)
        System.arraycopy(array2, 0, joinedArray, array1.size, array2.size)
        return joinedArray
    }

    fun addSalt(bytes: ByteArray) = mergeByteArrays(bytes, getSalt())

}