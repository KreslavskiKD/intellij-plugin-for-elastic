package com.github.kreslavskikd.intellijpluginforelastic.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.io.File

import java.util.*

@Service(Service.Level.PROJECT)
class ElasticProjectService(project: Project) {

    fun downloadLogs(baseUrl: String, outputDir: String, query: String): String {
        val endpoint = "/_search"

        val headers = mapOf("Content-Type" to "application/json")

        try {
            val response = khttp.get(baseUrl + endpoint, headers = headers, data = query)
            File(outputDir).mkdirs()
            val outputFile = File("$outputDir/data.log")
            outputFile.writeText(response.text)
            thisLogger().info("data logs stored to: " + outputFile.absolutePath)
            return response.text
        } catch (e: Exception) {
            thisLogger().warn("failed to load data:")
            e.cause?.let { thisLogger().warn(it) }
            e.message?.let { thisLogger().warn(it) }
        }
        return ""
    }
}
