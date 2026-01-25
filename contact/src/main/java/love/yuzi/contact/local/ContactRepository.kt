package love.yuzi.contact.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import love.yuzi.contact.model.Contact
import love.yuzi.contact.model.toContact

/**
 * 1. 获取联系人列表Flow，使用 order_index 升序
 * 2. 获取单个联系人，可能为 null
 * 3. 插入一个联系人列表，插入时需要生成 order_index
 * 4. 修改联系人顺序，需要插入全量的联系人 lookupKey 列表
 * 5. 更新联系人信息，需要传入 DomainModel 列表
 * 6. 删除联系人，需要传入 lookupKey
 */
class ContactRepository (
    private val contactDao: ContactDao,
) {

    fun getContactsFlow(): Flow<List<Contact>> = contactDao.getContactsFlow().map { contacts ->
        contacts.map { it.toContact() }
    }

    suspend fun getContact(lookupKey: String) = contactDao.getContact(lookupKey)?.toContact()

    suspend fun insertContacts(contacts: List<Contact>) = contactDao.insertContacts(contacts)

    suspend fun updateOrderIndex(contacts: List<String>) = contactDao.updateOrderIndex(contacts)

    suspend fun updateContacts(contacts: List<Contact>) = contactDao.updateContacts(contacts)

    suspend fun deleteContact(lookupKey: String) = contactDao.deleteContact(lookupKey)
}
