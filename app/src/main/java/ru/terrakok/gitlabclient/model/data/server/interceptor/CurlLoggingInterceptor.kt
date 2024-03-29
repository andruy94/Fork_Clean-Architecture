/*
 * Copyright (C) 2016 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Edited by @terrakok
 */

package ru.terrakok.gitlabclient.model.data.server.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor.Logger
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

/**
 * An OkHttp interceptor that logs requests as curl shell commands. They can then
 * be copied, pasted and executed inside a terminal environment. This might be
 * useful for troubleshooting client/server API interaction during development,
 * making it easy to isolate and share requests made by the app.
 *
 * Warning: The
 * logs generated by this interceptor have the potential to leak sensitive
 * information. It should only be used in a controlled manner or in a
 * non-production environment.
 */
class CurlLoggingInterceptor(private val logger: Logger = Logger.DEFAULT) : Interceptor {

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }

    private var curlOptions: String? = null
        /** Set any additional curl command options (see 'curl --help').  */
        set

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var compressed = false

        var curlCmd = "curl"
        if (curlOptions != null) {
            curlCmd += " " + curlOptions!!
        }
        curlCmd += " -X " + request.method()

        val headers = request.headers()
        for (i in 0 until headers.size()) {
            val name = headers.name(i)
            var value = headers.value(i)

            val start = 0
            val end = value.length - 1
            if (value[start] == '"' && value[end] == '"') {
                value = "\\\"" + value.substring(1, end) + "\\\""
            }

            if ("Accept-Encoding".equals(name, ignoreCase = true) && "gzip".equals(value, ignoreCase = true)) {
                compressed = true
            }
            curlCmd += " -H \"$name: $value\""
        }

        request.body()?.let {
            val buffer = Buffer().apply { it.writeTo(this) }
            val charset = it.contentType()?.charset(UTF8) ?: UTF8
            // try to keep to a single line and use a subshell to preserve any line breaks
            curlCmd += " --data $'" + buffer.readString(charset).replace("\n", "\\n") + "'"
        }

        curlCmd += (if (compressed) " --compressed " else " ") + "\"${request.url()}\""

        logger.log("╭--- cURL (" + request.url() + ")")
        logger.log(curlCmd)
        logger.log("╰--- (copy and paste the above line to a terminal)")

        return chain.proceed(request)
    }
}