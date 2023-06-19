package com.github.kreslavskikd.intellijpluginforelastic.util

import java.net.MalformedURLException
import java.net.URL

fun isValidUrl(url: String?): Boolean {
    return try {
        URL(url)
        true
    } catch (e: MalformedURLException) {
        false
    }
}