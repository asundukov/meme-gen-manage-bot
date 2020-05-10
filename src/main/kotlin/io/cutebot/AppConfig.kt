package io.cutebot

import io.cutebot.markonimagemanage.service.LogFileInterceptor
import io.cutebot.markonimagemanage.service.LogInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {
    @Bean
    fun telegramRestTemplate(): RestTemplate {
        val httpFactory = SimpleClientHttpRequestFactory()
        httpFactory.setReadTimeout(66000)
        httpFactory.setConnectTimeout(66000)

        val factory: ClientHttpRequestFactory = BufferingClientHttpRequestFactory(httpFactory)

        val restTemplate = RestTemplate(factory)

        restTemplate.interceptors = listOf(LogInterceptor())

        return restTemplate
    }

    @Bean
    fun telegramFileRestTemplate(): RestTemplate {
        val httpFactory = SimpleClientHttpRequestFactory()
        httpFactory.setReadTimeout(10000)
        httpFactory.setConnectTimeout(10000)


        val restTemplate = RestTemplate(httpFactory)

        restTemplate.interceptors = listOf(LogFileInterceptor())

        return restTemplate
    }
}