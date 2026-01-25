package love.yuzi.dialer.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.components.Avatar
import love.yuzi.dialer.components.TextAvatar
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme

@Composable
fun lightPrimaryOverlay(alpha: Float = 0.2f): Color {
    val background = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    return primary.copy(alpha = alpha).compositeOver(background)
}


@Composable
fun SimpleContactItem(
    contact: Contact,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(12.dp),
    selected: Boolean = false,
    onClick: (Contact) -> Unit = {}
) {
    Box(
        modifier = modifier
            .padding(
                vertical = if (selected) 4.dp else 0.dp
            )
    ) {
        val shape = RoundedCornerShape(16.dp)
        var realModifier = Modifier
            .fillMaxWidth()
            .clip(shape = shape)
        if (selected) {
            realModifier = realModifier
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = shape
                )
                .background(lightPrimaryOverlay())
        }

        Row(
            modifier = realModifier
                .clickable { onClick(contact) }
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.Start
            )
        ) {
            if (contact.avatarUri.isNullOrEmpty().not()) {
                Avatar(
                    data = contact.avatarUri!!,
                    enableRounded = true,
                    contentDescription = { contact.name }
                )
            } else {
                TextAvatar(
                    text = contact.name.last().uppercase(),
                    enableRounded = true,
                    enableBold = true
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = contact.phone,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private val previewContact = Contact(
    lookupKey = "lookup",
    name = "王思玉",
    phone = "123 4567 8901",
    avatarUri = null,
    lastUpdatedTimestamp = 0
)

@Preview
@Composable
private fun SimpleContactItemPreview() {
    AssistiveDialerTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            SimpleContactItem(
                selected = true,
                contact = previewContact
            )

            SimpleContactItem(
                contact = previewContact
            )

            SimpleContactItem(
                selected = true,
                contact = previewContact
            )

            SimpleContactItem(
                contact = previewContact
            )
        }
    }
}