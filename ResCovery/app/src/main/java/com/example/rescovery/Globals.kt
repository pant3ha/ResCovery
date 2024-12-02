package com.example.rescovery

// This is to save keys that may have to be used across multiple files

abstract class Globals {
    companion object {
        const val PREF_CUR_USER_NAME = "current userid"
        const val PREF_CUR_USER_KEY = "current userid key"

        const val EVENT_ID_KEY = "event id key"
        const val EVENT_RESTAURANT_KEY = "event restaurant key"
        const val EVENT_INVITEES_KEY = "event invitees key"
    }
}