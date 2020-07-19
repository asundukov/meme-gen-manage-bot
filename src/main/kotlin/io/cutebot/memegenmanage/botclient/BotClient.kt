package io.cutebot.memegenmanage.botclient

import io.cutebot.memegenmanage.botclient.model.*
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
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
    private val apiUrl = "$url/api/manage"
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

    fun getMeme(memeId: Int): GetMemeResponse {
        return get("/memes/$memeId", GetMemeResponse::class.java)
    }

    fun getMemes(botId: Int): GetListMemeResponse {
        return get("/memes/findByBot?bot_id=$botId", GetListMemeResponse::class.java)
    }

    fun addMeme(memeRequest: CreateMemeRequest) {
        val url = "$apiUrl/memes"
        val reqEntity = MultipartEntity()
        reqEntity.addPart("image", FileBody(File(memeRequest.imagePath)))
        reqEntity.addPart("alias", StringBody(memeRequest.alias,
                ContentType.create(TEXT_PLAIN.mimeType, Charsets.UTF_8)))
        reqEntity.addPart("textAreaCoords", StringBody(memeRequest.textAreaCoords, TEXT_PLAIN))
        reqEntity.addPart("botId", StringBody(memeRequest.botId.toString(), TEXT_PLAIN))

        return postMultipartData(reqEntity, url)
    }

    fun updateMeme(memeRequest: UpdateMemeRequest, memeId: Int): GetMemeResponse {
        val url = "/memes/$memeId"

        return post(url, memeRequest, GetMemeResponse::class.java)
    }

    fun deleteMeme(memeId: Int) {
        val url = "/memes/$memeId"
        delete(url)
    }

    fun getMemeImageUrl(memeId: Int): String {
        return "$url/meme/$memeId/image"
    }

    private fun <T> get(path: String, cls: Class<T>): T {
        val fullUrl = apiUrl + path
        log.info("GET $fullUrl")
        val entity = HttpEntity<Any>(defaultHeaders)
        return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, cls).body!!
    }

    private fun <T> post(path: String, data: Any, cls: Class<T>): T {
        val fullUrl = apiUrl + path
        log.info("POST $fullUrl")
        val entity = HttpEntity(data, defaultHeaders)
        return restTemplate.exchange(fullUrl, HttpMethod.POST, entity, cls).body!!
    }

    private fun delete(path: String) {
        val fullUrl = apiUrl + path
        log.info("DELETE $fullUrl")
        val entity = HttpEntity<Any>(defaultHeaders)
        restTemplate.exchange(fullUrl, HttpMethod.DELETE, entity, Any::class.java)
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