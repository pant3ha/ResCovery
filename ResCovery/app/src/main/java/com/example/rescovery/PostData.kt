package com.example.rescovery

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

//this is static data for testing
object PostData {
    fun getPosts(): List<UserInput> {
        //list of random posts
        return listOf(
            UserInput(
                id = 1,
                userId = 1,
                restaurantId = 16,
                comment = "this is a test",
                rating = 5.0,
                photoUri = "android.resource://com.example.rescovery/drawable/test_image1",
                userName = "panteha"
            ),
            UserInput(
                id = 2,
                userId = 2,
                restaurantId = 16,
                comment = "this is a test",
                rating = 5.0,
                photoUri = "android.resource://com.example.rescovery/drawable/test_image2",
                userName = "defaultUser"
            )
        )
    }
}