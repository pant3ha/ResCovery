package com.example.rescovery.post_data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post (
    val publisher: String? = "",
    val restaurant: Int? = null,
    val rating: Float? = null,
    val review: String? = "",
    val image: String? = ""

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "publisher" to publisher,
            "restaurant" to restaurant,
            "rating" to rating,
            "review" to review,
            "image" to image,
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(publisher)
        parcel.writeValue(restaurant)
        parcel.writeValue(rating)
        parcel.writeString(review)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}