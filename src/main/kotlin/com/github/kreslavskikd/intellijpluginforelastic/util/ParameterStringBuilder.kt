package com.github.kreslavskikd.intellijpluginforelastic.util

import java.net.URLEncoder
import java.lang.StringBuilder

object ParameterStringBuilder {
    fun getParamsString(params: MutableMap<String, String>): String {
        val result = StringBuilder()
        for ((key, value) in params.entries) {
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
            result.append("&")
        }

        val resultString = result.toString()
        return if (resultString.isNotEmpty()) resultString.substring(0, resultString.length - 1) else resultString
    }
}