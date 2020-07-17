package io.cutebot.markonimagemanage.botclient

import io.cutebot.markonimagemanage.botclient.model.*
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType.TEXT_PLAIN
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.File

@Service
class BotClient(
        @Value("\${mainbot.url}")
        private val url: String,

        @Value("\${mainbot.access-token}")
        private val accessToken: String
) {
    private val restTemplate = RestTemplate()
    private val defaultHeaders = LinkedMultiValueMap<String, String>()
    init {
        defaultHeaders["X-Access-Token"] = accessToken
    }

    fun addBot(userId: Long, token: String, title: String): GetBotResponse {
        return post("/bots", CreateBotRequest(token, userId, title), GetBotResponse::class.java)
    }

    fun updateBot(botId: Int, updateBotRequest: UpdateBotRequest): GetBotResponse {
        return post("/bots/$botId", updateBotRequest, GetBotResponse::class.java)
    }

    fun getBot(botId: Int): GetBotResponse {
        return get("/bots/$botId", GetBotResponse::class.java)
    }

    fun getBots(userId: Long): GetListBotResponse {
        return get("/bots?user_id=$userId", GetListBotResponse::class.java)
    }

    fun getMarks(botId: Int): GetListMarkResponse {
        return get("/marks/findByBot?bot_id=$botId", GetListMarkResponse::class.java)
    }

    private fun <T> get(path: String, cls: Class<T>): T {
        val fullUrl = url + path
        log.info("GET $fullUrl")
        val entity = HttpEntity<Any>(defaultHeaders)
        return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, cls).body!!
    }

    private fun <T> post(path: String, data: Any, cls: Class<T>): T {
        val fullUrl = url + path
        log.info("POST $fullUrl")
        val entity = HttpEntity(data, defaultHeaders)
        return restTemplate.exchange(fullUrl, HttpMethod.POST, entity, cls).body!!
    }

    fun addMark(markRequest: CreateMarkRequest) {
        val url = "$url/marks"
        val reqEntity = MultipartEntity()
        reqEntity.addPart("image", FileBody(File(markRequest.imagePath)))
        reqEntity.addPart("title", StringBody(markRequest.title, TEXT_PLAIN))
        reqEntity.addPart("description", StringBody(markRequest.description, TEXT_PLAIN))
        reqEntity.addPart("botId", StringBody(markRequest.botId.toString(), TEXT_PLAIN))
        reqEntity.addPart("position", StringBody(markRequest.position.toString(), TEXT_PLAIN))
        reqEntity.addPart("sizeValue", StringBody(markRequest.sizeValue.toString(), TEXT_PLAIN))
        reqEntity.addPart("opacity", StringBody(markRequest.opacity.toString(), TEXT_PLAIN))

        return postMultipartData(reqEntity, url)
    }

    private fun postMultipartData(entity: MultipartEntity, url: String) {
        val httpclient: HttpClient = DefaultHttpClient()
        val httpPost = HttpPost(url)
        httpPost.entity = entity
        httpPost.setHeader("X-Access-Token", accessToken)

        log.info("Multpart request to {}", url)
        val response = httpclient.execute(httpPost)

        val r = String(response.entity.content.readAllBytes())
        log.info("Result: {}", r)
    }



    companion object {
        private val log = LoggerFactory.getLogger(BotClient::class.java)
    }
}