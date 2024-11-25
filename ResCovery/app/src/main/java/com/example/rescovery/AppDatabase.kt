package com.example.rescovery

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Restaurant::class, UserInput::class], version = 4, exportSchema = false)
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
                        AppDatabase::class.java, "app_database")
                        //.addCallback(DatabaseCallback(context))
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    /*private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                val userInputDao = database.userInputDao
                val restaurantDao = database.restaurantDatabaseDao

                val posts = PostData.getPosts()
                val restaurants = RestaurantData.getRestaurants()
                restaurantDao.insertAll(restaurants)
                userInputDao.insertAll(posts)
                Log.d("AppDatabase", "added default data to databases: $restaurants")
            }
        }
    }*/

}