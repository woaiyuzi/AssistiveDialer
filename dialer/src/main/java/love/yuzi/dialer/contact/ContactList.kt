package love.yuzi.dialer.contact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import kotlin.time.Clock

@Preview(showBackground = true)
@Composable
private fun ContactListPreview() {
    val contacts = List(5) {
        Contact(
            lookupKey = "lookup_key_$it",
            name = "王思玉_$it",
            phone = "123 4567 890$it",
            avatarUri = null,
            lastUpdatedTimestamp = Clock.System.now().epochSeconds + it
        )
    }

    AssistiveDialerTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ContactList(contacts = contacts) { contact ->
                SimpleContactItem(contact = contact, modifier = Modifier.padding(12.dp))
            }
        }
    }
}

@Suppress("UnstableCollections")
@Composable
fun ContactList(
    contacts: List<Contact>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    itemContent: @Composable LazyItemScope.(Contact) -> Unit,
) {
    LazyColumn(
        state = state,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        items(
            items = contacts,
            key = { contact -> contact.lookupKey },
            contentType = { "contact_item" }) { contact ->
            itemContent(contact)
        }
    }
}
