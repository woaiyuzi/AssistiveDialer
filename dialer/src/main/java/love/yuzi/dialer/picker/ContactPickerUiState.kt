package love.yuzi.dialer.picker

import love.yuzi.contact.model.Contact
import love.yuzi.dialer.contact.ContactUiState

data class ContactPickerUiState(
    val contactUiState: ContactUiState = ContactUiState(isLoading = true),
    var selectedContacts: List<Contact> = emptyList(),
    val excludeLookupKeys: List<String> = emptyList()
)
