package io.cutebot.markonimagemanage.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import java.io.IOException
import java.nio.charset.Charset

class LogInterceptor : ClientHttpRequestInterceptor {
    @Throws(IOException::class)
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val req = String(body)

        log.info("request to {} with {}", request.uri.toString(), req)
        try {
            val resp = execution.execute(request, body)

            val res = StreamUtils.copyToString(resp.body, Charset.defaultCharset())
            log.info("response: {}", res)

            return resp
        } catch (e: Exception) {
            log.info("ResourceAccessException with response: {}", e.message)
            throw e;
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogInterceptor::class.java)
    }
}