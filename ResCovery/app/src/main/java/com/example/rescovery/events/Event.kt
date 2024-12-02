package com.example.rescovery.events

data class Event(
    var restaurantId: Long = 0L,
    val hostName: String = "",
    var dateTime: Long = 0L
)
