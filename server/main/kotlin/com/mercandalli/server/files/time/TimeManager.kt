package com.mercandalli.server.files.time

interface TimeManager {

    fun getDayString(): String

    fun getTimeString(): String

    fun getTimeLong(): Long
}