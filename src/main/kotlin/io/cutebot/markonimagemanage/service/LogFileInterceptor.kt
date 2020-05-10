package io.cutebot.markonimagemanage.service

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.IOException

class LogFileInterceptor : ClientHttpRequestInterceptor {
    @Throws(IOException::class)
    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val req = String(body)

        log.info("request: {}", req)

        val resp = execution.execute(request, body)

        log.info("response length: {} bytes", resp.headers["content-length"])

        return resp
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogFileInterceptor::class.java)
    }
}