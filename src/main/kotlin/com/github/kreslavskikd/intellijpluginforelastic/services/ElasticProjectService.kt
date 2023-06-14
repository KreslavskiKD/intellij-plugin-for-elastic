package com.github.kreslavskikd.intellijpluginforelastic.services

import com.github.kreslavskikd.intellijpluginforelastic.repo.QueryType
import com.github.kreslavskikd.intellijpluginforelastic.util.ParameterStringBuilder
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


@Service(Service.Level.PROJECT)
class ElasticProjectService(private val project: Project) {

    fun downloadLogs(baseUrl: String, outputDir: String, query: String, queryType: QueryType): String {
        val endpoint = "/_search"

        val headers = mapOf("Content-Type" to "application/json")

        val url = URL(baseUrl + endpoint)
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Content-Type", "application/json")
        con.connectTimeout = 15000
        con.readTimeout = 15000


        when (queryType) {
            QueryType.QUERY_PARAMS -> {
                return try {
                    val parameters: MutableMap<String, String> = HashMap()

                    val lines = query.split("\n")
                    for (line in lines) {
                        val kv = line.split("=")
                        parameters[kv[0]] = kv[1]
                    }

                    con.doOutput = true
                    val out = DataOutputStream(con.outputStream)
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters))
                    out.flush()
                    out.close()
                    val status = con.responseCode

                    val streamReader = if (status > 299) {
                        InputStreamReader(con.errorStream)
                    } else {
                        InputStreamReader(con.inputStream)
                    }

                    val responseStream = BufferedReader(
                        streamReader
                    )
                    var inputLine = ""
                    val content = StringBuffer()
                    while (responseStream.readLine().also { inputLine = it } != null) {
                        content.append(inputLine)
                    }
                    responseStream.close()
                    content.toString()
                } catch (e: Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                } finally {
                    con.disconnect();
                }
            }
            QueryType.QUERY_STRING -> {
                return try {
                    con.doOutput = true
                    val out = DataOutputStream(con.outputStream)
                    out.writeBytes(query)
                    out.flush()
                    out.close()
                    val status = con.responseCode
                    val streamReader = if (status > 299) {
                        InputStreamReader(con.errorStream)
                    } else {
                        InputStreamReader(con.inputStream)
                    }

                    val responseStream = BufferedReader(
                        streamReader
                    )
                    var inputLine = ""
                    val content = StringBuffer()
                    while (responseStream.readLine().also { inputLine = it } != null) {
                        content.append(inputLine)
                    }
                    responseStream.close()
                    content.toString()
                } catch (e : Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                } finally {
                    con.disconnect();
                }
            }
            QueryType.JSON -> { // todo rewrite without khttp usage
                return try {
                    val response = khttp.get(baseUrl + endpoint, headers = headers, data = query)
//                    File(outputDir).mkdirs()
//                    val outputFile = File("$outputDir/data.log")
//                    outputFile.writeText(response.text)
//                    thisLogger().info("data logs stored to: " + outputFile.absolutePath)
                    response.text
                } catch (e: Exception) {
                    thisLogger().warn("failed to load data:")
                    e.cause?.let { thisLogger().warn(it) }
                    e.message?.let { thisLogger().warn(it) }
                    ("failed to load data:\n" + (e.cause ?: "") + " " + (e.message ?: ""))
                }
            }
        }
    }

    fun createScratchLogFile(text: String) {

    }
}
