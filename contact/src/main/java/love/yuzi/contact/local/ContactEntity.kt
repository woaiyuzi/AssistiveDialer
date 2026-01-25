package love.yuzi.contact.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contacts",
    indices = [
        Index(value = ["order_index"]),
    ]
)
data class ContactEntity(
    @PrimaryKey
    @ColumnInfo(name = "lookup_key")
    val lookupKey: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "avatar_uri")
    val avatarUri: String?,

    @ColumnInfo(name = "last_updated_timestamp")
    val lastUpdatedTimestamp: Long,

    /**
     * 该属性仅用于数据库返回数据时的排序，外部不可以使用
     */
    @ColumnInfo(name = "order_index")
    val orderIndex: Int?
)