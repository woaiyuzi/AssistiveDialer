package love.yuzi.dialer.picker

import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import love.yuzi.contact.local.ContactRepository
import love.yuzi.contact.model.Contact
import love.yuzi.contact.system.SystemContactRepository
import love.yuzi.dialer.contact.ContactUiEvent
import love.yuzi.dialer.contact.ContactUiState
import love.yuzi.dialer.contact.ContactViewModel
import timber.log.Timber

@HiltViewModel
class ContactPickerViewModel @Inject constructor(
    private val systemContactRepository: SystemContactRepository,
    private val contactRepository: ContactRepository,
) : ContactViewModel() {
    private val _uiState = MutableStateFlow(ContactPickerUiState())
    val uiState = _uiState.asStateFlow()

    private fun Contact.shouldBeIncluded(
        excludeLookupKeys: Set<String>,
        includeNoAvatar: Boolean
    ): Boolean =
        lookupKey !in excludeLookupKeys &&
                (includeNoAvatar || avatarUri != null)

    private fun formatPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }

        val regex = """^(\d{1,3})(\d{0,4})(\d{0,4})$""".toRegex()
        val matchResult = regex.find(digits) ?: return digits

        val (part1, part2, part3) = matchResult.destructured

        return buildString {
            append(part1)
            if (part2.isNotEmpty()) append(" ").append(part2)
            if (part3.isNotEmpty()) append(" ").append(part3)
        }.trim()
    }

    fun loadContacts(
        excludeLookupKeys: List<String> = emptyList(),
        includeNoAvatar: Boolean = false
    ) {
        safeLaunch(
            onError = { e ->
                _uiState.update { it.copy(contactUiState = ContactUiState(isLoading = false)) }
                Timber.e(e, "Failed to load contacts")
                sendUiEvent(ContactUiEvent.LoadFailed)
            }
        ) {
            _uiState.update {
                it.copy(
                    contactUiState = ContactUiState(isLoading = true),
                    excludeLookupKeys = excludeLookupKeys
                )
            }

            val excludeSet = excludeLookupKeys.toHashSet()

            val systemContacts = systemContactRepository.getContacts()
                .filter { it.shouldBeIncluded(excludeSet, includeNoAvatar) }
                .map {
                    it.copy(phone = formatPhone(it.phone))
                }

            _uiState.update {
                it.copy(
                    contactUiState = ContactUiState(contacts = systemContacts, isLoading = false)
                )
            }
            Timber.d("Loaded ${systemContacts.size} system contacts")
        }
    }

    fun toggleSelect(contact: Contact) {
        _uiState.update {
            if (it.selectedContacts.contains(contact)) {
                it.copy(selectedContacts = it.selectedContacts - contact)
            } else {
                it.copy(selectedContacts = it.selectedContacts + contact)
            }
        }
    }

    fun saveContacts() {
        safeLaunch(
            onError = { e ->
                Timber.e(e, "Failed to save contacts")
            }
        ) {
            if (uiState.value.selectedContacts.isNotEmpty()) {
                contactRepository.insertContacts(uiState.value.selectedContacts)
                Timber.d("Saved ${uiState.value.selectedContacts.size} contacts")
                _uiState.update {
                    it.copy(selectedContacts = emptyList())
                }
            }
        }
    }
}