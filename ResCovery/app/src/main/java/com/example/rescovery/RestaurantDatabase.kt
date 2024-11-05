package com.example.rescovery

import UserInput
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Restaurant::class, UserInput::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {
    abstract val restaurantDatabaseDao: RestaurantDatabaseDao
    abstract val userInputDao: UserInputDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null

        fun getInstance(context: Context) : RestaurantDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        RestaurantDatabase::class.java, "restaurant_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}