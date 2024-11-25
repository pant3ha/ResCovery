package com.example.rescovery

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Restaurant::class, UserInput::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val restaurantDatabaseDao: RestaurantDatabaseDao
    abstract val userInputDao: UserInputDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "app_database").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}