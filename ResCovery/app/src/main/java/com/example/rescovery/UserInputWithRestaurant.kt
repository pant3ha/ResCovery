package com.example.rescovery

import UserInput
import androidx.room.Embedded
import androidx.room.Relation

data class UserInputWithRestaurant(
    @Embedded val userInput: UserInput,

    @Relation(
        parentColumn = "restaurant_id",
        entityColumn = "id"
    )
    val restaurant: Restaurant)