package love.yuzi.contact.model

import love.yuzi.contact.local.ContactEntity

data class Contact(
    val lookupKey: String,
    val name: String,
    val phone: String,
    val avatarUri: String?,
    val lastUpdatedTimestamp: Long
)

internal fun Contact.toEntity(
    orderIndex: Int
): ContactEntity {
    return ContactEntity(
        lookupKey = lookupKey,
        name = name,
        phone = phone,
        avatarUri = avatarUri,
        orderIndex = orderIndex,
        lastUpdatedTimestamp = lastUpdatedTimestamp
    )
}

internal fun ContactEntity.toContact(): Contact {
    return Contact(
        lookupKey = lookupKey,
        name = name,
        phone = phone,
        avatarUri = avatarUri,
        lastUpdatedTimestamp = lastUpdatedTimestamp
    )
}