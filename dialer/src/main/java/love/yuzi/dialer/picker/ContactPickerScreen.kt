package love.yuzi.dialer.picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.R
import love.yuzi.dialer.contact.ContactList
import love.yuzi.dialer.contact.ContactUiState
import love.yuzi.dialer.contact.SimpleContactItem
import love.yuzi.dialer.contact.uiEventHandler
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme

@Suppress("UnstableCollections")
@Composable
fun ContactPickerScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactPickerViewModel = hiltViewModel(),
    includeNoAvatar: Boolean = false,
    excludeLookupKeys: List<String> = emptyList()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiEventHandler(viewModel)

    LaunchedEffect(viewModel) {
        viewModel.loadContacts(excludeLookupKeys, includeNoAvatar)
    }

    ContactPickerContent(
        modifier = modifier,
        uiState = uiState,
        onBack = {
            viewModel.saveContacts()
            onFinish()
        },
        onContactSelect = {
            viewModel.toggleSelect(it)
        }
    )
}

@Composable
private fun ContactPickerContent(
    uiState: ContactPickerUiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onContactSelect: (Contact) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 300.dp),
    ) {
        TopBar(uiState = uiState, onFinish = onBack)

        if (uiState.contactUiState.isLoading) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round
                )
            }
            return@Column
        }

        if (uiState.contactUiState.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.contact_empty))
            }
        } else {
            val hapticFeedback = LocalHapticFeedback.current

            ContactList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contacts = uiState.contactUiState.contacts
            ) { contact ->
                SimpleContactItem(
                    modifier = Modifier.animateItem(),
                    contact = contact,
                    onClick = {
                        onContactSelect(contact)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    },
                    selected = uiState.selectedContacts.contains(contact)
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    uiState: ContactPickerUiState,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧标题
        Text(
            text = stringResource(R.string.contact_picker_title),
            style = MaterialTheme.typography.titleMedium,
        )

        // 右侧完成按钮
        TextButton(
            enabled = uiState.selectedContacts.isNotEmpty(),
            onClick = onFinish,
            // 关键：减少默认内边距，防止按钮离右边缘太远
            contentPadding = PaddingValues(horizontal = 8.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(
                    R.string.contact_picker_finish,
                    uiState.selectedContacts.size
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactPickerPreview() {
    val contacts = List(5) {
        Contact(
            lookupKey = "lookup_key_$it",
            name = "王思玉",
            phone = "123 0815 0145$it",
            lastUpdatedTimestamp = it.toLong(),
            avatarUri = null
        )
    }

    AssistiveDialerTheme {
        ContactPickerContent(
            uiState = ContactPickerUiState(
                contactUiState = ContactUiState(contacts = contacts, isLoading = true)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactEmptyContentPreview() {
    AssistiveDialerTheme {
        ContactPickerContent(
            uiState = ContactPickerUiState(
                contactUiState = ContactUiState(contacts = emptyList(), isLoading = true)
            )
        )
    }
}