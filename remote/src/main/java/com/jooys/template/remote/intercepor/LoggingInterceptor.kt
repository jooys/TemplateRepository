package com.jooys.template.remote.intercepor

import com.orhanobut.logger.Logger
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.io.IOException
import java.lang.Long
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val logBody = true

        val requestBody = request.body
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1
        var requestStartMessage = "--> " + request.method + ' '.toString() + request.url + ' '.toString() + protocol

        val logHeader = StringBuilder()
        if (!logBody && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }
        logHeader.append(requestStartMessage)
        logHeader.append("\n")

        if (hasRequestBody) {
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody!!.contentType() != null) {
                logHeader.append("Content-Type: " + requestBody.contentType())
                logHeader.append("\n")
            }
            if (requestBody.contentLength().toInt() != -1) {
                logHeader.append("Content-Length: " + requestBody.contentLength())
                logHeader.append("\n")
            }
        }

        val headers = request.headers
        var i = 0
        val count = headers.size
        while (i < count) {
            val name = headers.name(i)
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                logHeader.append(name + ": " + headers.value(i))
                logHeader.append("\n")
            }
            i++
        }

        if (!logBody || !hasRequestBody) {
            logHeader.append("--> END " + request.method)
            logHeader.append("\n")
        } else if (bodyEncoded(request.headers)) {
            logHeader.append("--> END " + request.method + " (encoded body omitted)")
            logHeader.append("\n")
        } else {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)

            var charset: Charset? = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }

            logHeader.append("")
            logHeader.append("\n")
            if (isPlaintext(buffer)) {
                charset?.let {
                    var json = buffer.readString(it)
                    json = json.trim()
                    if (json.startsWith("{")) {
                        val jsonObject = JSONObject(json)
                        val message = jsonObject.toString(2)
                        logHeader.append(message)
                    }
                    if (json.startsWith("[")) {
                        val jsonArray = JSONArray(json)
                        val message = jsonArray.toString(2)
                        logHeader.append(message)
                    }
                }
//                logHeader.append(Gson().toJson())
                logHeader.append("\n")
                logHeader.append("--> END " + request.method + " (" + requestBody.contentLength() + "-byte body)")
            } else {
                logHeader.append("--> END " + request.method + " (binary " + requestBody.contentLength() + "-byte body omitted)")
            }
        }
        Logger.d(logHeader)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Logger.d("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()

        val responseLog = StringBuilder()

        responseLog.append("<-- " + response.code + ' '.toString() + response.message + ' '.toString() + response.request.url + " (" + tookMs + "ms" + ')'.toString())
        responseLog.append("\n")


        if (logBody) {
            val headers = response.headers
            var i = 0
            val count = headers.size
            while (i < count) {
                responseLog.append(headers.name(i) + ": " + headers.value(i))
                responseLog.append("\n")
                i++
            }

            if (!logBody || !response.promisesBody()) {
                responseLog.append("<-- END HTTP")
                responseLog.append("\n")
                Logger.d(responseLog)
            } else if (bodyEncoded(response.headers)) {
                responseLog.append("<-- END HTTP (encoded body omitted)")
                responseLog.append("\n")
                Logger.d(responseLog)
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE)
                //Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset? = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                if (!isPlaintext(buffer)) {
                    responseLog.append("")
                    responseLog.append("\n")
                    responseLog.append("<-- END HTTP (binary " + buffer.size + "-byte body omitted)")
                    responseLog.append("\n")
                    Logger.d(responseLog)
                    return response
                }
                Logger.d(responseLog)

                if (contentLength != 0L) {
                    charset?.let {
                        try {
                            Logger.json(buffer.clone().readString(it))
                        } catch (e: JSONException) {
                            Logger.w(buffer.clone().readString(it))
                        }
                    }
                }
            }
        }

        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false
            }
        }
    }
}
