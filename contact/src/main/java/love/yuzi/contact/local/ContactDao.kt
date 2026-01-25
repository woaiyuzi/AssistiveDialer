package love.yuzi.contact.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import love.yuzi.contact.model.Contact
import love.yuzi.contact.model.toEntity

/**
 * 1. 获取联系人列表Flow，使用 order_index 升序
 * 2. 获取单个联系人，可能为 null
 * 3. 插入一个联系人列表，插入时需要生成 order_index
 * 4. 修改联系人顺序，需要插入全量的联系人 lookupKey 列表
 * 5. 更新联系人信息，需要传入 DomainModel 列表
 * 6. 删除联系人，需要传入 lookupKey
 */
@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY order_index ASC")
    fun getContactsFlow(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE lookup_key = :lookupKey")
    suspend fun getContact(lookupKey: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInternal(contact: ContactEntity)

    @Query("SELECT MAX(order_index) FROM contacts")
    fun getMaxOrderIndex(): Int?

    @Transaction
    suspend fun insertContacts(contacts: List<Contact>) {
        val maxOrderIndex = getMaxOrderIndex() ?: 0
        contacts.forEachIndexed { index, contact ->
            insertInternal(contact.toEntity(orderIndex = maxOrderIndex + 1 + index))
        }
    }

    @Query("UPDATE contacts SET order_index = :orderIndex WHERE lookup_key = :lookupKey")
    fun updateOrderIndexInternal(lookupKey: String, orderIndex: Int)

    @Transaction
    suspend fun updateOrderIndex(contacts: List<String>) {
        contacts.forEachIndexed { index, lookupKey ->
            updateOrderIndexInternal(lookupKey, index)
        }
    }

    @Query("UPDATE contacts SET name = :name, phone = :phone, avatar_uri = :avatarUri, last_updated_timestamp = :lastUpdatedTimestamp WHERE lookup_key = :lookupKey")
    fun updateContactInternal(
        lookupKey: String,
        name: String,
        phone: String,
        avatarUri: String?,
        lastUpdatedTimestamp: Long
    )

    @Transaction
    suspend fun updateContacts(contacts: List<Contact>) {
        contacts.forEach {
            updateContactInternal(
                lookupKey = it.lookupKey,
                name = it.name,
                phone = it.phone,
                avatarUri = it.avatarUri,
                lastUpdatedTimestamp = it.lastUpdatedTimestamp
            )
        }
    }

    @Query("DELETE FROM contacts WHERE lookup_key = :lookupKey")
    suspend fun deleteContact(lookupKey: String)
}