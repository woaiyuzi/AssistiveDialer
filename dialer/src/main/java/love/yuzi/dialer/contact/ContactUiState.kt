package love.yuzi.dialer.contact

import androidx.compose.runtime.Immutable
import love.yuzi.contact.model.Contact

@Immutable
data class ContactUiState(
    val contacts: List<Contact> = emptyList(),

    val isLoading: Boolean = false,

    val isRefreshing: Boolean = false
) {
    val isEmpty = contacts.isEmpty()
}