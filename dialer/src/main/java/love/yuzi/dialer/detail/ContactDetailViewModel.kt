package love.yuzi.dialer.detail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import love.yuzi.contact.local.ContactRepository
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.contact.ContactUiEvent
import love.yuzi.dialer.contact.ContactViewModel
import timber.log.Timber

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: ContactRepository
) : ContactViewModel() {

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact = _contact.asStateFlow()

    fun loadContact(lookupKey: String) {
        viewModelScope.launch {
            _contact.value = repository.getContact(lookupKey)
        }
    }

    fun deleteContact() {
        safeLaunch(
            onError = {
                Timber.e(it, "Failed to delete contact by lookup key: ${_contact.value?.lookupKey}")
                sendUiEvent(ContactUiEvent.OperationFailed)
            }
        ) {
            _contact.value?.let {
                repository.deleteContact(it.lookupKey)
            }
        }
    }
}