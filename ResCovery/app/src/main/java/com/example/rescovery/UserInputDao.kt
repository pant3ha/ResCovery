package com.example.rescovery

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInputDao {
    @Insert
    suspend fun insert(userInput: UserInput)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userInputs: List<UserInput>)

    @Transaction
    @Query("SELECT * FROM user_input_table")
    fun getAllUserInputs(): Flow<List<UserInputWithRestaurant>> //for getting all user inputs for feed

    @Transaction
    @Query("SELECT * FROM user_input_table WHERE user_id = :userId")
    fun getUserInputs(userId: Long): Flow<List<UserInputWithRestaurant>> //for displaying all posts a specific user has made for the profile

    @Transaction
    @Query("SELECT * FROM user_input_table WHERE restaurant_id = :restaurantId")
    fun getAllUserInputsForRestaurant(restaurantId: Long): Flow<List<UserInputWithRestaurant>> //for displaying all posts associated with a specific restaurant

    @Delete
    suspend fun delete(userInput: UserInput)
}