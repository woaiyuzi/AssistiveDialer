package love.yuzi.dialer.manager

import dagger.hilt.android.lifecycle.HiltViewModel
import love.yuzi.contact.local.ContactRepository
import love.yuzi.contact.model.Contact
import love.yuzi.contact.system.SystemContactRepository
import love.yuzi.dialer.contact.ContactUiEvent
import love.yuzi.dialer.home.HomeViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContactManagerViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    systemContactRepository: SystemContactRepository
) : HomeViewModel(contactRepository, systemContactRepository) {
    fun updateContactsOrderIndex(contacts: List<Contact>) {
        if (contacts.isEmpty()) return

        safeLaunch(
            onError = { e ->
                Timber.e(e, "Failed to update contacts order index")
                sendUiEvent(ContactUiEvent.OperationFailed)
            }
        ) {
            contactRepository.updateOrderIndex(contacts.map { it.lookupKey })
            Timber.d("Updated contacts order index")
        }
    }
}