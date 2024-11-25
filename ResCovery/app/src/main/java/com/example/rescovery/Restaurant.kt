package com.example.rescovery

import android.os.Parcelable
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurant_table")
data class Restaurant (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "restaurant_name")
    var restaurantName: String = "",

    @ColumnInfo(name = "address")
    var restaurantAddress: String = "",

    @ColumnInfo(name = "coordinates")
    var coordinates: String = "",

    @ColumnInfo(name = "overall_rating")
    var overallRating: Double = 0.0,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "price_range")
    var priceRange: String = "",

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String = "",
    )