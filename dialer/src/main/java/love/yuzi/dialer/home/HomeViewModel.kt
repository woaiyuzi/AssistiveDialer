package love.yuzi.dialer.home

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import love.yuzi.contact.local.ContactRepository
import love.yuzi.contact.model.Contact
import love.yuzi.contact.system.SystemContactRepository
import love.yuzi.dialer.contact.ContactUiEvent
import love.yuzi.dialer.contact.ContactUiState
import love.yuzi.dialer.contact.ContactViewModel
import timber.log.Timber

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val systemContactRepository: SystemContactRepository
) : ContactViewModel() {

    private val _contactsFlow = contactRepository.getContactsFlow()

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<ContactUiState> =
        combine(_contactsFlow, _isRefreshing) { contacts, refreshing ->
            ContactUiState(
                contacts = contacts,
                isRefreshing = refreshing,
                isLoading = false
            )
        }
            .onStart {
                emit(ContactUiState(isLoading = true))
            }
            .catch {
                Timber.e(it, "Failed to load contacts")
                sendUiEvent(ContactUiEvent.LoadFailed)
                emit(ContactUiState(isLoading = false, isRefreshing = false))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = ContactUiState(isLoading = true)
            )

    private fun Contact.isNewerThan(other: Contact): Boolean =
        lastUpdatedTimestamp > other.lastUpdatedTimestamp

    private fun Contact.isDifferentFrom(other: Contact): Boolean =
        phone != other.phone ||
                name != other.name ||
                avatarUri != other.avatarUri

    private suspend fun updateContacts() {
        val systemContacts = systemContactRepository.getContacts()
        val originalContacts = uiState.value.contacts
        if (originalContacts.isEmpty() || systemContacts.isEmpty()) {
            return
        }

        val updatedContacts = systemContacts.filter { systemContact ->
            originalContacts.any { originalContact ->
                originalContact.lookupKey == systemContact.lookupKey &&
                        systemContact.isNewerThan(originalContact) &&
                        systemContact.isDifferentFrom(originalContact)
            }
        }

        contactRepository.updateContacts(updatedContacts)
        Timber.d("Updated ${updatedContacts.size} contacts")
        if (updatedContacts.isNotEmpty()) {
            sendUiEvent(ContactUiEvent.Updated(updatedContacts.size))
        }
    }

    fun silentRefreshContacts() {
        safeLaunch(
            onError = { e ->
                Timber.e(e, "Failed to refresh contacts but silently")
                sendUiEvent(ContactUiEvent.OperationFailed)
            }) {
            updateContacts()
        }
    }

    fun pullRefreshContacts() {
        safeLaunch(
            onError = { e ->
                Timber.e(e, "Failed to refresh contacts but pull refresh")
                sendUiEvent(ContactUiEvent.OperationFailed)
            }, onFinally = {
                _isRefreshing.value = false
            }
        ) {
            updateContacts()
            _isRefreshing.value = true
        }
    }
}