package love.yuzi.contact.system

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import love.yuzi.contact.model.Contact
import kotlin.coroutines.CoroutineContext

/**
 * 系统联系人的唯一操作入口
 */
class SystemContactRepository(
    private val context: Context,
    private val dispatcher: CoroutineContext = Dispatchers.IO
) {
    /**
     * 此函数仅查询所有联系人，不进行过滤，不进行数据格式化
     *
     * 查询条件：必须有 displayName，phone
     *
     * 查询的属性：lookupKey, displayName, phone(多个时取第一个), photoUri, lastUpdatedTimestamp
     *
     * @return 本地化升序排列的联系人列表
     */
    suspend fun getContacts(): List<Contact> = withContext(dispatcher) {
        val resolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
        )

        val selection = buildString {
            append(" ${ContactsContract.Contacts.DISPLAY_NAME} !=? AND ")
            append(" ${ContactsContract.Contacts.HAS_PHONE_NUMBER} > ?")
        }

        val selectionArgs = arrayOf("''", "0")

        val sortOrder = "${ContactsContract.Contacts.DISPLAY_NAME} COLLATE LOCALIZED ASC"

        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            cursor.asSequence().map {
                buildContact(it, resolver)
            }.toList()
        }
    } ?: emptyList()

    private fun Cursor.asSequence(): Sequence<Cursor> = sequence {
        while (moveToNext()) yield(this@asSequence)
    }

    private fun buildContact(
        cursor: Cursor,
        resolver: ContentResolver,
    ): Contact {
        val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
        val phone = queryPhone(resolver, id)

        val name =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
        val lookupKey =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.LOOKUP_KEY))
        val avatarUri =
            cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
        val lastUpdatedTimestamp =
            cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))

        val contact = Contact(
            lookupKey = lookupKey,
            name = name,
            phone = phone!!,
            avatarUri = avatarUri,
            lastUpdatedTimestamp = lastUpdatedTimestamp
        )

        return contact
    }

    private fun queryPhone(resolver: ContentResolver, contactId: String): String? {
        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )?.use { pc ->
            if (pc.moveToFirst()) {
                return pc.getString(pc.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
        }
        return null
    }
}