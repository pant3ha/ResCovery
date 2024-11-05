import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import com.example.rescovery.Restaurant

@Entity(tableName = "user_input_table")
data class UserInput(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "user_id")
    var userId: Long = 0L,

    @ColumnInfo(name = "restaurant_id")
    var restaurantId: Long = 0L,

    @ColumnInfo(name = "comment")
    var comment: String = "",

    @ColumnInfo(name = "rating")
    var rating: Double = 0.0,

    @ColumnInfo(name = "photo_uri")
    var photoUri: String? = null,

    @ColumnInfo(name = "user_name")
    var userName: String = "default"
)