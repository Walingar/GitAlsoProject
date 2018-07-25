package com.jetbrains.gitalso.storage.log.send

import com.google.common.net.HttpHeaders
import com.google.gson.Gson
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.reporting.StatsSender
import com.intellij.util.io.HttpRequests
import org.apache.commons.codec.binary.Base64OutputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

// TODO: update it
object LogSender {
    private const val infoUrl = "https://www.jetbrains.com/config/features-service-status.json"
    private val LOG = logger<LogSender>()

    private class StatsServerInfo(@JvmField var status: String,
                                  @JvmField var url: String,
                                  @JvmField var urlForZipBase64Content: String) {
        fun isServiceAlive() = "ok" == status
    }

    fun send(text: String, compress: Boolean = true): Boolean {
        val info = requestServerUrl() ?: return false
        try {
            executeRequest(info, text, compress)
            return true
        } catch (e: Exception) {
            LOG.debug(e)
        }
        return false
    }

    private val gson by lazy { Gson() }

    private fun requestServerUrl(): StatsServerInfo? {
        try {
            val info = gson.fromJson(HttpRequests.request(infoUrl).readString(), StatsServerInfo::class.java)
            if (info.isServiceAlive()) return info
        } catch (e: Exception) {
            LOG.debug(e)
        }

        return null
    }

    private fun executeRequest(info: StatsServerInfo, text: String, compress: Boolean) {
        if (compress) {
            val data = Base64GzipCompressor.compress(text)
            HttpRequests
                    .post(info.urlForZipBase64Content, null)
                    .tuner { it.setRequestProperty(HttpHeaders.CONTENT_ENCODING, "gzip") }
                    .write(data)
            return
        }

        HttpRequests.post(info.url, "text/html").write(text)
    }

    private object Base64GzipCompressor {
        fun compress(text: String): ByteArray {
            val outputStream = ByteArrayOutputStream()
            val base64Stream = GZIPOutputStream(Base64OutputStream(outputStream))
            base64Stream.write(text.toByteArray())
            base64Stream.close()
            return outputStream.toByteArray()
        }
    }

}