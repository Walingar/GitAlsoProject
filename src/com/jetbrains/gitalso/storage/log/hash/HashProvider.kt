package com.jetbrains.gitalso.storage.log.hash

object HashProvider {
    fun hash(st: String): Long {
        val p = 31L
        var hash = 0L
        var pPow = 1L
        for (ch in st) {
            hash += (ch - 'a' + 1) * pPow
            pPow *= p
        }
        return hash
    }
}