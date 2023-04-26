package com.github.kreslavskikd.intellijpluginforelastic.repo

object InfoRepo {

    var elasticAddress: String = "http://localhost"

    var logsDir: String = "./logs"

    var query: String = """{"query": {"match_all": {}}}"""
}

object Constants {
    const val ELASTIC_PORT = "9200"
}