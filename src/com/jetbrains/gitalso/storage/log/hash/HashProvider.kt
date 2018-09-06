package com.jetbrains.gitalso.storage.log.hash

import java.security.MessageDigest

object HashProvider {

    private fun bytesToLong(bytes: ByteArray?): Long {
        if (bytes == null) {
            return 0
        }
        val p = 31L
        var hash = 0L
        var pPow = 1L
        for (ch in bytes) {
            hash += ch * pPow
            pPow *= p
        }
        return hash
    }

    fun hash(st: String): Long {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(st.toByteArray())
        return bytesToLong(md.digest())
    }
}