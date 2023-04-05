package com.github.kreslavskikd.intellijpluginforelastic.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.kreslavskikd.intellijpluginforelastic.MyBundle

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

@Service(Service.Level.PROJECT)
class KibanaProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
    }


    fun sendKibanaRequest(): String {
//        val url = "http://localhost:5601/api/reporting/generate/csv"
//        val url = "https://www.google.ru/"
//        val username = "elastic"
//        val password = "changeme"
//
//        val credentialsProvider = BasicCredentialsProvider()
//        credentialsProvider.setCredentials(AuthScope.ANY, UsernamePasswordCredentials(username, password))
//        val httpClient: CloseableHttpClient = HttpClients.custom()
//            .setDefaultCredentialsProvider(credentialsProvider)
//            .build()
//
//        val request = HttpPost(url)
//
//        val entity = StringEntity("{}",
//            ContentType.APPLICATION_JSON)
//
//        request.entity = entity
//        request.addHeader(BasicScheme.authenticate(UsernamePasswordCredentials(username, password), "UTF-8", false))
//        request.addHeader("kbn-xsrf", "true")
//
//        val response = httpClient.execute(request)
//        val responseBody: String? = EntityUtils.toString(response.entity)
//        EntityUtils.consumeQuietly(response.entity)

        val url = "http://localhost:5601/api/spaces/space/default"

        val httpClient = HttpClientBuilder.create().build()
        val request = HttpGet(url)
        request.setHeader("kbn-xsrf", "true")
        val response = httpClient.execute(request)
        val entity = response.entity
        val responseBody = entity.content.bufferedReader().use { it.readText() }

        return  responseBody
    }
}
