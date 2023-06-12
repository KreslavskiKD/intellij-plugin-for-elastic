package com.github.kreslavskikd.intellijpluginforelastic.repo

object InfoRepo {

    var elasticAddress: String = "http://localhost"

    var logsDir: String = "./logs"

    var query: String = ""

    var selectedQueryType = QueryType.QUERY_PARAMS
}

object Constants {
    const val ELASTIC_PORT = "9200"

    const val queryBaseStart = """{"query": """
    const val queryBaseEnd = "}}"
}

enum class QueryType {
    QUERY_STRING, QUERY_PARAMS, JSON
}