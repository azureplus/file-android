package com.mercandalli.server.files.log

import io.ktor.request.ApplicationRequest

interface LogManager {

    fun d(tag: String, message: String)

    fun logRequest(tag: String, request: ApplicationRequest)
}